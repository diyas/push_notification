package com.push.app.main.controller;

import com.push.app.model.TrStatusEnum;
import com.push.app.model.domain.PaymentMethodView;
import com.push.app.model.domain.Transaction;
import com.push.app.model.payload.MessageParam;
import com.push.app.model.payload.RequestPaymentStatus;
import com.push.app.model.payload.RequestPayment;
import com.push.app.repository.PaymentMethodRepo;
import com.push.app.repository.TransactionRepo;
import com.push.app.service.MqttService;
import com.push.app.utility.Utility;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentStatusCtl {

    private String pointerCode = "CLD";

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransactionRepo trRepo;

    @Autowired
    private PaymentMethodRepo payRepo;

    @PostMapping(value = "/v1/payment/publish")
    public ResponseEntity<String> publishPayment(@RequestBody RequestPayment request) throws MqttException {
        PaymentMethodView method = payRepo.findById(request.getTrMethod().code - 1);
        mqttService.connect();
        Transaction tr = new Transaction();
        tr.setTrNo(pointerCode+"-"+Utility.getUser()+"-"+System.currentTimeMillis());
        tr.setUserId(Utility.getUser());
        tr.setTrAmount(request.getTrAmount());
        tr.setTrMethod(request.getTrMethod().code - 1);
        tr.setTrTopicEdc("payment/pos/status/" + method.getId() + "/" + Utility.getUser());
        tr.setTrTopicPos("payment/pos/" + Utility.getUser());
        tr.setTrStatus(TrStatusEnum.PENDING);
        Transaction trResult = null;
        if (mqttService.isConnected())
            trResult = trRepo.save(tr);
        if (trResult != null) {
            String msg = Utility.objectToString(new MessageParam(method.getPaymentName().toLowerCase(), tr.getTrAmount(), tr.getTrNo()));
            mqttService.publish(tr.getTrTopicPos(), msg);
            System.out.println(msg);
        }
        else
            return Utility.setResponse("payment failed published", null);
        mqttService.disconnect();
        return Utility.setResponse("payment has been published", tr);
    }

    @PutMapping(value = "/v1/payment/publish/status")
    public ResponseEntity<String> publishPaymentStatus(@RequestBody RequestPaymentStatus request) throws MqttException {
        Transaction tr = trRepo.findByTrNoAndTrStatus(request.getTrNo(), TrStatusEnum.PENDING);
        if (tr == null)
            return Utility.setResponse("payment status completed", null);
        tr.setTrStatus(TrStatusEnum.COMPLETED);
        trRepo.save(tr);
        if (tr.getId() != 0) {
            mqttService.connect();
            mqttService.publish(tr.getTrTopicEdc(), Utility.objectToString(tr));
            mqttService.disconnect();
        }
        return Utility.setResponse("payment has been success", tr);
    }

    @GetMapping(value = "/v1/payment/status/{trNo}")
    public ResponseEntity<String> getStatusPayment(@PathVariable String trNo) {
        Transaction tr = trRepo.findByTrNo(trNo);
        if (tr == null)
            return Utility.setResponse(TrStatusEnum.INVALID.toString(), null);
        return Utility.setResponse(tr.getTrStatus().toString(), tr);
    }
}

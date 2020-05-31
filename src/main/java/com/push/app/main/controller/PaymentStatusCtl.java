package com.push.app.main.controller;

import com.push.app.config.MqttConfig;
import com.push.app.model.TrStatus;
import com.push.app.model.domain.PaymentMethod;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentStatusCtl {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransactionRepo trRepo;

    @Autowired
    private PaymentMethodRepo payRepo;

    @PostMapping(value = "/v1/payment/publish")
    @ResponseBody
    public ResponseEntity<String> publishPayment(@RequestBody RequestPayment request) throws MqttException {
        PaymentMethod method = payRepo.findById(request.getTrMethod());
        mqttService.connect();
        Transaction tr = new Transaction();
        tr.setTrNo(UUID.randomUUID().toString());
        tr.setTrAmount(request.getTrAmount());
        tr.setTrMethod(request.getTrMethod());
        tr.setTrTopicEdc("payment/pos/status/" + method.getId() + "/" + Utility.getUser());
        tr.setTrTopicPos("payment/pos/" + Utility.getUser());
        tr.setTrStatus(TrStatus.PENDING);
        Transaction trResult = null;
        if (mqttService.isConnected())
            trResult = trRepo.save(tr);
        if (trResult != null)
            mqttService.publish(tr.getTrTopicPos(), Utility.objectToString(new MessageParam(method.getMethodName().toLowerCase(), tr.getTrAmount(), method.getQrName())));
        else
            return Utility.setResponse("payment failed published", null);
        mqttService.disconnect();
        return Utility.setResponse("payment has been published", tr);
    }

    @PutMapping(value = "/v1/payment/publish/status")
    @ResponseBody
    public ResponseEntity<String> publishPaymentStatus(@RequestBody RequestPaymentStatus request) throws MqttException {
        Transaction tr = trRepo.findByTrNoAndTrStatus(request.getTrNo(), TrStatus.PENDING);
        if (tr == null)
            return Utility.setResponse("payment status completed", null);
        tr.setTrStatus(TrStatus.COMPLETED);
        trRepo.save(tr);
        if (tr.getId() != 0) {
            mqttService.connect();
            mqttService.publish(tr.getTrTopicEdc(), Utility.objectToString(tr));
            mqttService.disconnect();
        }
        return Utility.setResponse("payment has been success", tr);
    }

    @GetMapping(value = "/v1/payment/get_status/{trNo}")
    @ApiIgnore
    public ResponseEntity<String> getStatusPayment(@PathVariable String trNo) {
        Transaction tr = trRepo.findByTrNoAndTrStatus(trNo, TrStatus.COMPLETED);
        if (tr == null)
            return Utility.setResponse(TrStatus.INVALID.toString(), null);
        return Utility.setResponse(TrStatus.COMPLETED.toString(), tr);
    }
}

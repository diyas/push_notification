package com.push.app.main.controller;

import com.push.app.model.TrStatus;
import com.push.app.model.domain.PaymentMethodView;
import com.push.app.model.domain.Transaction;
import com.push.app.model.payload.MessageParam;
import com.push.app.model.payload.RequestPaymentStatus;
import com.push.app.model.payload.RequestPayment;
import com.push.app.repository.PaymentMethodRepo;
import com.push.app.repository.TransactionRepo;
import com.push.app.service.MqttService;
import com.push.app.utility.Utility;
import io.swagger.annotations.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping(value = "/api", produces = APPLICATION_JSON_VALUE)
@Api(value = "/api", tags = "PaymentStatus")
public class PaymentStatusCtl {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransactionRepo trRepo;

    @Autowired
    private PaymentMethodRepo payRepo;

    @PostMapping(value = "/v1/payment/publish")
    @ApiOperation(
            value = "Publish Payment", notes = "Returns Payment Data." +
            "error conditions",
            response = Transaction.class)
    public ResponseEntity<String> publishPayment(@ApiParam(value = "Request Body Parameter", required = true)
                                                     @RequestBody RequestPayment request) throws MqttException {
        PaymentMethodView method = payRepo.findById(request.getTrMethod());
        mqttService.connect();
        Transaction tr = new Transaction();
        tr.setTrNo("CLD-"+Utility.getUser()+"-"+System.currentTimeMillis());
        tr.setUserId(Utility.getUser());
        tr.setTrAmount(request.getTrAmount());
        tr.setTrMethod(request.getTrMethod());
        tr.setTrTopicEdc("payment/pos/status/" + method.getId() + "/" + Utility.getUser());
        tr.setTrTopicPos("payment/pos/" + Utility.getUser());
        tr.setTrStatus(TrStatus.PENDING);
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
    @ApiOperation(
            value = "Publish Payment by TrId", notes = "Returns Payment Data." +
            "error conditions",
            response = Transaction.class)
    public ResponseEntity<String> publishPaymentStatus(@ApiParam(value = "Request Body Parameter", required = true)
                                                           @RequestBody RequestPaymentStatus request) throws MqttException {
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

    @GetMapping(value = "/v1/payment/status/{trNo}")
    public ResponseEntity<String> getStatusPayment(@PathVariable String trNo) {
        Transaction tr = trRepo.findByTrNo(trNo);
        if (tr == null)
            return Utility.setResponse(TrStatus.INVALID.toString(), null);
        return Utility.setResponse(tr.getTrStatus().toString(), tr);
    }

    @GetMapping(value = "/v1/payment/pending")
    public ResponseEntity<List<Transaction>> getListPending(@ApiParam(value = "Request Body Parameter", required = true)
                                                                @RequestBody Map map){
        String trNo = map.get("trNo").toString();
        String userId = map.get("userId").toString();
        List<Transaction> lstData = null;
        if (!trNo.equalsIgnoreCase(""))
            lstData = trRepo.findAllByTrNo(trNo);
        else
            lstData = trRepo.findByUserIdAndTrStatus(userId, TrStatus.PENDING);
        if (lstData.size() == 0){
            return Utility.setResponse("pending payment not found", null);
        }
        return Utility.setResponse("", lstData);
    }

    @PutMapping(value = "/v1/payment/cancel/{trNo}")
    public ResponseEntity<List<Transaction>> cancelPending(@PathVariable String trNo) throws MqttException{
        mqttService.connect();
        Transaction tr = trRepo.findByTrNoAndTrStatus(trNo, TrStatus.PENDING);
        if (tr == null)
            return Utility.setResponse("pending payment not found", null);
        tr.setTrStatus(TrStatus.CANCEL);
        Transaction trUpd = trRepo.save(tr);
        if (trUpd != null) {
            mqttService.connect();
            mqttService.publish(tr.getTrTopicEdc(), Utility.objectToString(trUpd));
            mqttService.disconnect();
        }
        return Utility.setResponse("", trUpd);
    }
}

package com.push.app.main.controller;

import com.push.app.config.MessageType;
import com.push.app.config.MqttConfig;
import com.push.app.model.TrStatus;
import com.push.app.model.domain.Transaction;
import com.push.app.model.payload.Payload;
import com.push.app.model.payload.RequestPaymentStatus;
import com.push.app.model.payload.RequestPayment;
import com.push.app.model.payload.Response;
import com.push.app.repository.TransactionRepo;
import com.push.app.service.MqttService;
import com.push.app.utility.Utility;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api")
public class PaymentStatusCtl {

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransactionRepo trRepo;

    //@PostMapping(value = "/v1/payment")
    public ResponseEntity<String> actionMessage(@RequestBody Payload payload) {
        String TOPIC = payload.getParam().getTopic() + payload.getSerialNumber();
        try {
            //mqttService.connect(false);
            if (payload.getMessageType().equals(MessageType.PUBLISH)) {
                mqttService.publish(TOPIC, payload.getParam().getMessage());
                return new ResponseEntity<String>("message has been published", HttpStatus.OK);
            } else if (payload.getMessageType().equals(MessageType.SUBSCRIBE)) {
                mqttService.subscribe(payload.getParam().getTopic());
                return new ResponseEntity<String>("message has been subscribe", HttpStatus.OK);
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("message failed " + payload.getMessageType() + e.getMessage(), HttpStatus.OK);
        }
        return null;
    }

    // @GetMapping(value = "/v1/payment/status")
    public ResponseEntity<String> subscribeClient(@PathVariable String topic) {
        //trRepo.findByTrStatus()
        return new ResponseEntity<String>("message has been subscribe", HttpStatus.OK);
    }

    @PostMapping(value = "/v1/payment/publish")
    @ResponseBody
    public ResponseEntity<String> publishPayment(@RequestBody RequestPayment request) throws MqttException {
        Transaction tr = new Transaction();
        tr.setTrNo(request.getTrNo());
        tr.setTrAmount(request.getTrAmount());
        tr.setTrMethod(request.getTrMethod());
        tr.setTrTopicEdc("/Payment/Status/" + Utility.getUser());
        tr.setTrTopicPos("/Payment/" + Utility.getUser());
        tr.setTrStatus(TrStatus.PENDING);
//        boolean isConnected = mqttService.connect(false);

        Transaction trResult = null;
        if (MqttConfig.isConnected)
            trResult = trRepo.save(tr);
        if (trResult != null) {
            mqttService.publish(tr.getTrTopicPos(), Utility.objectToString(tr));
            //mqttService.subscribe(tr.getTrTopicPos());
        }
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
            if (!MqttConfig.isConnected)
                mqttService.connect(false);
            mqttService.publish(tr.getTrTopicEdc(), Utility.objectToString(tr));
        }
        return Utility.setResponse("payment has been success", tr);
    }

    @GetMapping(value = "/v1/payment/get_status/{trNo}")
    public ResponseEntity<String> getStatusPayment(@PathVariable String trNo) {
        Transaction tr = trRepo.findByTrNoAndTrStatus(trNo, TrStatus.COMPLETED);
        if (tr == null)
            return Utility.setResponse(TrStatus.INVALID.toString(), null);
        return Utility.setResponse(TrStatus.COMPLETED.toString(), tr);
    }
}

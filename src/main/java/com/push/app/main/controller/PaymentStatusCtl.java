package com.push.app.main.controller;

import com.push.app.config.MessageType;
import com.push.app.model.TrStatus;
import com.push.app.model.domain.Transaction;
import com.push.app.model.payload.Payload;
import com.push.app.model.payload.RequestPaymentStatus;
import com.push.app.model.payload.RequestPayment;
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

    @PostMapping(value = "/v1/payment")
    public ResponseEntity<String> actionMessage(@RequestBody Payload payload) {
        String TOPIC = payload.getParam().getTopic() + payload.getSerialNumber();
        try {
            mqttService.connect();
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

    @GetMapping(value = "/v1/payment/status")
    public ResponseEntity<String> subscribeClient(@PathVariable String topic) {
        //trRepo.findByTrStatus()
        return new ResponseEntity<String>("message has been subscribe", HttpStatus.OK);
    }

    @PostMapping(value = "/v1/payment/publish")
    public ResponseEntity<String> publishPayment(@RequestBody RequestPayment request) throws MqttException {
        Transaction tr = new Transaction();
        tr.setTrNo(request.getTrNo());
        tr.setTrAmount(request.getTrAmount());
        tr.setTrMethod(request.getTrMethod());
//        tr.setTrDate(new Date(System.currentTimeMillis()));
//        tr.setTrRequestDate(new Date(System.currentTimeMillis()));
        tr.setTrTopicEdc("/topicEdc/" + Utility.getUser());
        tr.setTrTopicPos("/topicPos/" + Utility.getUser());
        tr.setTrStatus(TrStatus.PENDING);
        boolean isConnected = mqttService.connect();
        Transaction trResult = null;
        if (isConnected)
            trResult = trRepo.save(tr);
        if (trResult != null) {
            mqttService.publish(tr.getTrTopicEdc(), Utility.objectToString(tr));
            //mqttService.subscribe(tr.getTrTopicPos());
        }
        return new ResponseEntity<String>("message has been publish", HttpStatus.OK);
    }

    @PutMapping(value = "/v1/payment/publish/status")
    public ResponseEntity<String> publishPaymentStatus(@RequestBody RequestPaymentStatus request) throws MqttException {
        Transaction tr = trRepo.findByTrNoAndTrStatus(request.getTrNo(), TrStatus.PENDING);
        if (tr == null)
            return new ResponseEntity<String>("payment status completed", HttpStatus.OK);
        tr.setTrStatus(TrStatus.COMPLETED);
        trRepo.save(tr);
        if (tr.getId() != 0) {
            mqttService.connect();
            mqttService.publish(tr.getTrTopicPos(), Utility.objectToString(tr));
        }
        return new ResponseEntity<String>("message has been publish", HttpStatus.OK);
    }

    @GetMapping(value = "/v1/payment/get_status/{trNo}")
    public ResponseEntity<String> getStatusPayment(@PathVariable String trNo) {
        Transaction tr = trRepo.findByTrNoAndTrStatus(trNo, TrStatus.COMPLETED);
        if (tr == null)
            return new ResponseEntity<String>("", HttpStatus.OK);
        return new ResponseEntity<String>(TrStatus.COMPLETED.toString(), HttpStatus.OK);
    }

}

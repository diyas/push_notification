package com.push.app.main.listener;

import com.push.app.model.TrStatusEnum;
import com.push.app.model.domain.PaymentMethodView;
import com.push.app.model.domain.Transaction;
import com.push.app.model.payload.MessageParam;
import com.push.app.model.payload.RequestPayment;
import com.push.app.repository.PaymentMethodRepo;
import com.push.app.repository.TransactionRepo;
import com.push.app.service.MqttService;
import com.push.app.utility.Utility;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentCloudListener {
    private String pointerCode = "CLD";

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransactionRepo trRepo;

    @Autowired
    private PaymentMethodRepo payRepo;

    public ResponseEntity<String> postSale(RequestPayment request) throws MqttException {
        boolean isNew = false;
        Transaction tr = null;
        PaymentMethodView method = payRepo.findById(request.getTrMethod());
        mqttService.connect();
        if (request.getTrNoPos() != null)
            tr = trRepo.findByTrNoPosAndTrStatus(request.getTrNoPos(), TrStatusEnum.PENDING);
        if (tr == null){
            tr = new Transaction();
            tr.setTrNo(pointerCode + "-" + Utility.getUser() + "-" + System.currentTimeMillis());
            tr.setUserId(Utility.getUser());
            tr.setTrAmount(request.getTrAmount());
            tr.setTrMethod(request.getTrMethod());
            tr.setTrTopicEdc("payment/pos/status/" + method.getId() + "/" + Utility.getUser());
            tr.setTrTopicPos("payment/pos/" + Utility.getUser());
            tr.setTrStatus(TrStatusEnum.PENDING);
            tr.setTrNoPos(request.getTrNoPos());
            isNew = true;
        }
        Transaction trResult = null;
        if (mqttService.isConnected() && isNew)
            trResult = trRepo.save(tr);
        if (trResult != null || isNew || request.isRepublish()) {
            String msg = Utility.objectToString(new MessageParam(method.getPaymentName().toLowerCase(), tr.getTrAmount(), tr.getTrNo()));
            mqttService.publish(tr.getTrTopicPos(), msg);
        }
        else {
            if (isNew) {
                return Utility.setResponse("payment failed published", null);
            } else {
                return Utility.setResponse("payment failed published, "+tr.getTrNoPos()+" already exists", tr);
            }
        }
        mqttService.disconnect();
        return Utility.setResponse("payment has been published", tr);
    }

    public void cancelTransaction(String trNo) throws Exception {
        final long start = System.currentTimeMillis();
        Transaction tr = trRepo.findByTrNoPosAndTrStatus(trNo, TrStatusEnum.PENDING);
        tr.setTrStatus(TrStatusEnum.CANCEL);
        trRepo.save(tr);
        log.info("Transaction ID "+ tr.getTrNoPos());
    }

    public void postVoid(){

    }

    public void postSettlement(){

    }
}

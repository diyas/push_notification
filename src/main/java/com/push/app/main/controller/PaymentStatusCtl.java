package com.push.app.main.controller;

import com.push.app.main.listener.PaymentCloudListener;
import com.push.app.model.TrStatusEnum;
import com.push.app.model.domain.PaymentMethodView;
import com.push.app.model.domain.Transaction;
import com.push.app.model.payload.MessageParam;
import com.push.app.model.payload.RequestPaymentStatus;
import com.push.app.model.payload.RequestPayment;
import com.push.app.model.payload.Response;
import com.push.app.repository.PaymentMethodRepo;
import com.push.app.repository.TransactionRepo;
import com.push.app.service.MqttService;
import com.push.app.utility.Utility;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api", produces = APPLICATION_JSON_VALUE)
@Api(value = "/api", tags = "Payment Status")
public class PaymentStatusCtl {

    private String pointerCode = "CLD";

    @Autowired
    private MqttService mqttService;

    @Autowired
    private TransactionRepo trRepo;

    @Autowired
    private PaymentMethodRepo payRepo;

    @Autowired
    private PaymentCloudListener listener;

    @PostMapping(value = "/v1/payment/publish")
    @ApiOperation(
            value = "Publish Payment", notes = "Subscribe Broker : payment/pos/{userid}",
            response = Response.class)
    public ResponseEntity<String> publishPayment(@ApiParam(value = "Request Body Parameter", required = true)
                                                     @RequestBody RequestPayment request) throws Exception {
        return listener.postSale(request);
    }

    @PutMapping(value = "/v1/payment/publish/status")
    @ApiOperation(
            value = "Publish Payment by TrId", notes = "",
            response = Response.class)
    public ResponseEntity<String> publishPaymentStatus(@ApiParam(value = "Request Body Parameter", required = true)
                                                           @RequestBody RequestPaymentStatus request) throws MqttException {
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
    @ApiOperation(value = "Check Payment Status", notes = "Check Payment Status", response = Response.class)
    public ResponseEntity<String> getStatusPayment(@PathVariable String trNo) {
        Transaction tr = trRepo.findByTrNo(trNo);
        if (tr == null)
            return Utility.setResponse(TrStatusEnum.INVALID.toString(), null);
        return Utility.setResponse(tr.getTrStatus().toString(), tr);
    }
}

package ru.flagman.paymentfail.services;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemEvent;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.flagman.paymentfail.processors.PaymentProcessor;

public class EvoIntegrationService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> processorMap = new HashMap<>();

        processorMap.put(
                PaymentSystemEvent.NAME_ACTION,
                new PaymentProcessor()
        );

        return processorMap;
    }
}

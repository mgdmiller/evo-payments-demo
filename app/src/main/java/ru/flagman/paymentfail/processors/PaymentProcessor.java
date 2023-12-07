package ru.flagman.paymentfail.processors;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.evotor.framework.core.action.event.receipt.payment.system.PaymentSystemProcessor;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemPaybackCancelEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemPaybackEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemSellCancelEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemSellEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.result.PaymentSystemPaymentErrorResult;
import ru.evotor.framework.core.action.event.receipt.payment.system.result.PaymentSystemPaymentOkResult;
import ru.evotor.framework.payment.PaymentType;

public class PaymentProcessor extends PaymentSystemProcessor {

    private static final String LOG_TAG = "PaymentProcessor";

    @Override
    public void payback(
            @NonNull String s,
            @NonNull PaymentSystemPaybackEvent paymentSystemPaybackEvent,
            @NonNull Callback callback
    ) {
        Log.e(LOG_TAG, "payback " + paymentSystemPaybackEvent);

        try {
            callback.skip();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void paybackCancel(
            @NonNull String s,
            @NonNull PaymentSystemPaybackCancelEvent paymentSystemPaybackCancelEvent,
            @NonNull Callback callback
    ) {
        Log.e(LOG_TAG, "paybackCancel " + paymentSystemPaybackCancelEvent);

        try {
            callback.skip();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sell(
            @NonNull String s,
            @NonNull PaymentSystemSellEvent paymentSystemSellEvent,
            @NonNull Callback callback
    ) {
        Log.e(LOG_TAG, "sell " + paymentSystemSellEvent);

        final UUID rrn = UUID.randomUUID();
        final List<String> slip = new ArrayList<>();

        slip.add("New slip 1");
        slip.add("New slip 2");

        try {
            callback.onResult(new PaymentSystemPaymentOkResult(rrn.toString(), slip, "", PaymentType.ELECTRON));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sellCancel(
            @NonNull String s,
            @NonNull PaymentSystemSellCancelEvent paymentSystemSellCancelEvent,
            @NonNull Callback callback
    ) {
        Log.e(LOG_TAG, "sellCancel " + paymentSystemSellCancelEvent);

        try {
            callback.onResult(new PaymentSystemPaymentErrorResult("Операция отменена"));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}

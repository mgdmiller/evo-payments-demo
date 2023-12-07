package ru.flagman.paymentfail.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.component.PaymentPerformerApi;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetExtra;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException;
import ru.evotor.framework.receipt.formation.api.SellApi;
import ru.flagman.paymentfail.R;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonPay).setOnClickListener((btn) -> doPay());
    }

    private void doPay() {
        final PackageManager pm = getPackageManager();
        final ArrayList<PositionAdd> positions = new ArrayList<>();
        final SetExtra extra = new SetExtra(new JSONObject());
        final PaymentPerformer performer = getPerformer("ru.poscredit.test", pm);

        positions.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                UUID.randomUUID().toString(),
                                null,
                                "Тестовый товар 1",
                                new Measure("ШТ", 0, 255),
                                new BigDecimal(1000),
                                new BigDecimal(1)
                        ).build()
                ));


        if (performer == null) {
            message("Не обнаружен подходящий процеесор оплат");
            return;
        }

        new OpenSellReceiptCommand(positions, extra, null)
                .process(
                        this,
                        future -> {
                            try {
                                final IntegrationManagerFuture.Result result = future.getResult();

                                if (result.getError() != null) {
                                    runOnUiThread(() -> message("Ошибка формирования чека: " + result.getError().getMessage()));
                                    return;
                                }

                                SellApi.moveCurrentReceiptDraftToPaymentStage(
                                        this,
                                        performer,
                                        new ReceiptFormationCallback() {
                                            @Override
                                            public void onSuccess() {
                                                runOnUiThread(() -> message("Чек успешно перемещен на стадию оплаты"));

                                            }

                                            @Override
                                            public void onError(@NonNull ReceiptFormationException e) {
                                                runOnUiThread(() -> message("Ошибка перемещения чека на стадию оплаты: " + e.getMessage()));
                                            }
                                        });

                            } catch (Exception e) {
                                runOnUiThread(() -> message("Ошибка получения результата формирования чека"));
                            }
                        });
    }

    private void message(String text) {
        new AlertDialog.Builder(this)
                .setMessage(text)
                .create()
                .show();
    }

    /**
     * Get payment performer by payment system id
     *
     * @param paymentSystemId id of payment system
     * @param pm              package manager
     */
    @Nullable
    private static PaymentPerformer getPerformer(@NotNull final String paymentSystemId, @NotNull final PackageManager pm) {
        final List<PaymentPerformer> performers = PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(pm);

        for (PaymentPerformer performer : performers) {
            Log.i(LOG_TAG, String.format("getPerformer: found performer %s / %s / %s",
                    performer.getAppName(), performer.getPackageName(), performer.getAppUuid()));
        }

        for (PaymentPerformer performer : performers) {
            final PaymentSystem paymentSystem = performer.getPaymentSystem();
            if (paymentSystem != null && paymentSystemId.equals(paymentSystem.getPaymentSystemId()))
                return performer;
        }

        return null;
    }
}
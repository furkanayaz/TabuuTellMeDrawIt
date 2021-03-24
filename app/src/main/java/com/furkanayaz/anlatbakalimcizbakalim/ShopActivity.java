package com.furkanayaz.anlatbakalimcizbakalim;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private BillingClient mBillingClient;

    private List<SkuDetails> skuINAPPDetailLists = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AdView adView;
    private CardView cardViewDefaultPackage, cardViewExtraPackage,cardviewShowPlayStore, cardViewHistoryPurchases, cardViewAllPurchase;
    private Random random = new Random();


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        cardViewDefaultPackage = findViewById(R.id.cardViewDefaultPackage);
        cardViewExtraPackage = findViewById(R.id.cardViewExtraPackage);
        cardviewShowPlayStore = findViewById(R.id.cardViewShowPlayStore);
        cardViewHistoryPurchases = findViewById(R.id.cardViewHistoryPurchases);
        cardViewAllPurchase = findViewById(R.id.cardViewAllPurchase);

        mBillingClient = BillingClient.newBuilder(ShopActivity.this).enablePendingPurchases().setListener(ShopActivity.this).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    cardViewStatus(true);

                    List<String> skuListINAPP = new ArrayList<>();

                    skuListINAPP.add("extrapackage");

                    SkuDetailsParams.Builder paramsINAPP = SkuDetailsParams.newBuilder();

                    paramsINAPP.setSkusList(skuListINAPP).setType(BillingClient.SkuType.INAPP);

                    mBillingClient.querySkuDetailsAsync(paramsINAPP.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                            skuINAPPDetailLists = list;
                        }
                    });


                }else {
                    Toast.makeText(ShopActivity.this,"Ödeme sistemi için Google Play hesabınızı kontrol ediniz",Toast.LENGTH_LONG).show();
                    cardViewStatus(false);

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(ShopActivity.this,"Ödeme sistemi şu anda geçerli değil",Toast.LENGTH_LONG).show();
                cardViewStatus(false);
            }
        });


        cardViewAllPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuINAPPDetailLists.get(0)).build();

                mBillingClient.launchBillingFlow(ShopActivity.this,billingFlowParams);

            }
        });


        Random random = new Random();
        int cardViewVisibility = random.nextInt(1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cardViewDefaultPackage.setVisibility(View.VISIBLE);
                cardViewExtraPackage.setVisibility(View.VISIBLE);
                cardviewShowPlayStore.setVisibility(View.VISIBLE);
                cardViewAllPurchase.setVisibility(View.VISIBLE);
            }
        },cardViewVisibility);

        cardViewDefaultPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShopActivity.this,"Mevcut paketi kullanmaktasınız",Toast.LENGTH_LONG).show();
            }
        });

        cardViewExtraPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Firebase Database'den verileri çek ve else komutunun altına aşağıdaki toast mesajını yazdır.

                Toast.makeText(ShopActivity.this,"Ek paketi kullanmamaktasınız",Toast.LENGTH_LONG).show();
            }
        });

        cardviewShowPlayStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferencesApprove = getSharedPreferences("PlayStoreComment",MODE_PRIVATE);
                SharedPreferences.Editor editorApprove = sharedPreferencesApprove.edit();
                editorApprove.clear();
                editorApprove.putInt("icommented",1);
                editorApprove.commit();

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

            }
        });

        cardViewHistoryPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, @Nullable List<PurchaseHistoryRecord> list) {

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                            StringBuilder sb = new StringBuilder();

                            for (PurchaseHistoryRecord purchaseHistoryRecord : list){
                                sb.append(purchaseHistoryRecord.getSku()+"\n");
                            }


                            AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                            builder.setTitle("SATIN ALIMLARIM");

                            TextView textViewHistoryPurchases = new TextView(ShopActivity.this);

                            textViewHistoryPurchases.setText(sb.toString());

                            builder.setView(textViewHistoryPurchases);

                            builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            builder.create().show();


                        }

                    }
                });
            }
        });


    }

    private void cardViewStatus(boolean status){
        cardViewAllPurchase.setEnabled(status);
        cardViewHistoryPurchases.setEnabled(status);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){

            for (Purchase purchase:list){

                if (!purchase.isAcknowledged()){
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                }

                if (purchase.getSku().equals("extrapackage")){
                    /*Shared Preferences ile paket alımını kaydet ve firebase database'e de kaydet kullanıcı hesabına giriş yaptığında hemen firebase database'den onay
                    sayısı al (0,1) eğer bir ise sharedpreferences'a kaydet.*/
                }

                Toast.makeText(ShopActivity.this,purchase.getSku()+ ": Ek paket satın alındı. İyi oyunlar :)",Toast.LENGTH_LONG).show();

            }

        }

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){

            Toast.makeText(ShopActivity.this,"Ödeme işlemi iptal edildi",Toast.LENGTH_LONG).show();

        }

    }
}
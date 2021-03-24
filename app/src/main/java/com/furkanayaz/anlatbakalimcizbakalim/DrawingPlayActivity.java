package com.furkanayaz.anlatbakalimcizbakalim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DrawingPlayActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private BillingClient billingClient;
    private List<SkuDetails> skuINAPPDetailList = new ArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RewardedAd rewardedAd;
    private RewardedAdLoadCallback rewardedAdLoadCallback;
    private RewardedAdCallback rewardedAdCallback;
    private CardView cardViewPalette,cardViewPen,cardViewMagenta,cardViewGreen,cardViewRed,cardViewBlue,cardViewDrawingPlayPause,cardViewClear,cardViewCorrectB,cardViewPassB;
    private ProgressBar progressBarDrawingPlay;
    private TextView textViewPlayDrawingTeamA,textViewPlaySecondsCounter,textViewPlaySecondsDrawingCounter,textViewPlayScore;
    private ConstraintLayout constraintLayoutThings;
    public static Path path = new Path();
    public static Paint paint_brush = new Paint();
    private View view;

    int index = 0;

    private Random random = new Random();
    private List<TabuuDrawing> tabuuDrawingList = new ArrayList<>();


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long START_TIME_IN_MILLIS = 0;
    private long TIME_LEFT_IN_MILLIS = 0;

    private List<Teams> teamsList = new ArrayList<>();
    private List<ScoreA> scoreAList = new ArrayList<>();
    private List<ScoreB> scoreBList = new ArrayList<>();


    //for Settings Preferences
    int seconds = 0;
    int pass = 0;



    //for Game Preferences
    int correctCounter = 0;
    int bothOfTeams = 0;
    int passCounterA = 0;
    int passCounterB = 0;
    int paletteVisibility = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_play);

        textViewPlayDrawingTeamA = findViewById(R.id.textViewPlayDrawingTeamA);
        textViewPlayScore = findViewById(R.id.textViewPlayScore);
        textViewPlaySecondsCounter = findViewById(R.id.textViewPlaySecondsCounter);
        textViewPlaySecondsDrawingCounter = findViewById(R.id.textViewPlaySecondsDrawingCounter);
        cardViewPalette = findViewById(R.id.cardViewPalette);
        cardViewPen = findViewById(R.id.cardViewPen);
        cardViewMagenta = findViewById(R.id.cardViewMagenta);
        cardViewGreen = findViewById(R.id.cardViewGreen);
        cardViewRed = findViewById(R.id.cardViewRed);
        cardViewBlue = findViewById(R.id.cardViewBlue);
        cardViewDrawingPlayPause = findViewById(R.id.cardViewDrawingPlayPause);
        cardViewClear = findViewById(R.id.cardViewClear);
        cardViewCorrectB = findViewById(R.id.cardViewCorrectB);
        cardViewPassB = findViewById(R.id.cardViewPassB);

        progressBarDrawingPlay = findViewById(R.id.progressBarDrawingPlay);

        constraintLayoutThings = findViewById(R.id.constraintLayoutThings);



        billingClient = BillingClient.newBuilder(DrawingPlayActivity.this).enablePendingPurchases().setListener(this).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    //cardViewStatusChanger(true);

                    List<String> skuListINAPP = new ArrayList<>();
                    skuListINAPP.add("150classictabuu");
                    skuListINAPP.add("300classictabuu");
                    skuListINAPP.add("50drawingtabuu");
                    skuListINAPP.add("100drawingtabuu");
                    skuListINAPP.add("noads");

                    SkuDetailsParams.Builder paramsINAPP = SkuDetailsParams.newBuilder();
                    paramsINAPP.setSkusList(skuListINAPP).setType(BillingClient.SkuType.INAPP);

                    billingClient.querySkuDetailsAsync(paramsINAPP.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                            skuINAPPDetailList = list;
                        }
                    });

                }else {
                    Toast.makeText(DrawingPlayActivity.this,"Ödeme sistemi için Google Play hesabınızı kontrol ediniz",Toast.LENGTH_LONG).show();
                    //cardViewStatusChanger(false);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(DrawingPlayActivity.this,"Ödeme işlemi sağlanamadı",Toast.LENGTH_LONG).show();
                //cardViewStatusChanger(false);

            }
        });



        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
        boolean buyprocess = sharedPreferences.getBoolean("buynoads",false);

        if (!buyprocess){

            MobileAds.initialize(DrawingPlayActivity.this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });

            //test: ca-app-pub-3940256099942544/5224354917
            //i have: ca-app-pub-5793841848623320/9042404436

            rewardedAd = new RewardedAd(DrawingPlayActivity.this,"ca-app-pub-5793841848623320/9042404436");
            rewardedAdLoadCallback = new RewardedAdLoadCallback(){
                @Override
                public void onRewardedAdLoaded() {

                }

                @Override
                public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {

                }
            };


            rewardedAd.loadAd(new AdRequest.Builder().build(),rewardedAdLoadCallback);
            rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdClosed() {

                    rewardedAd = new RewardedAd(DrawingPlayActivity.this,"ca-app-pub-5793841848623320/9042404436");
                    rewardedAdLoadCallback = new RewardedAdLoadCallback(){
                        @Override
                        public void onRewardedAdLoaded() {

                        }

                        @Override
                        public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {

                        }
                    };


                    rewardedAd.loadAd(new AdRequest.Builder().build(),rewardedAdLoadCallback);
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                }
            };

        }





        cardViewPen.setVisibility(View.INVISIBLE);
        cardViewMagenta.setVisibility(View.INVISIBLE);
        cardViewRed.setVisibility(View.INVISIBLE);
        cardViewGreen.setVisibility(View.INVISIBLE);
        cardViewBlue.setVisibility(View.INVISIBLE);



        sharedPreferences = getSharedPreferences("TeamDrawingNames",MODE_PRIVATE);
        String teamA = sharedPreferences.getString("teamDrawingA","A TAKIMI");
        String teamB = sharedPreferences.getString("teamDrawingB","B TAKIMI");
        Teams teams = new Teams(teamA,teamB);
        teamsList.add(teams); //onBackPressed da hem classic play de hem de drawing play de bunları clear et.
        textViewPlayDrawingTeamA.setText(teamsList.get(0).getTeamAname());


        sharedPreferences = getSharedPreferences("ClassicSettings",MODE_PRIVATE);

        seconds = sharedPreferences.getInt("classicseconds",180);
        pass = sharedPreferences.getInt("classicpass",5);

        passCounterA = pass;
        passCounterB = pass;
        bothOfTeams = pass;


        addTabuuDrawingCard();
        getRandomList(tabuuDrawingList);

        getTabuuDrawingCard();


        if (seconds == 180){
            START_TIME_IN_MILLIS = 180000;
            TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        }

        if (seconds == 150){
            START_TIME_IN_MILLIS = 150000;
            TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        }

        if (seconds == 120){
            START_TIME_IN_MILLIS = 120000;
            TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        }

        if (seconds == 90){
            START_TIME_IN_MILLIS = 90000;
            TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        }

        if (seconds == 60){
            START_TIME_IN_MILLIS = 60000;
            TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        }

        if (seconds == 30){
            START_TIME_IN_MILLIS = 30000;
            TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        }


        progressBarDrawingPlay.setMax(seconds);


        cardViewCorrectB.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (tabuuDrawingList.size() == 3 || tabuuDrawingList.size() == 1){
                    Snackbar.make(v,"Maalesef tabu kartınız bitti. Tabu kartı satın almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (firebaseUser != null){

                                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuINAPPDetailList.get(2)).build();

                                        billingClient.launchBillingFlow(DrawingPlayActivity.this,flowParams);

                                        Snackbar.make(v,"Tabuu kartlarınız başarılı bir şekilde satın alındı :)",Snackbar.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(DrawingPlayActivity.this,"Satın alımlarınızı uygulamaya üye olarak gerçekleştiriniz",Toast.LENGTH_LONG).show();
                                    }





                                }
                            }).show();
                }else {
                    correctCounter++;
                    textViewPlayScore.setText("SKOR: "+correctCounter);

                    pauseTimer();

                    path.reset();

                    getRandomList(tabuuDrawingList);

                    getTabuuDrawingCard();
                }



            }
        });

        cardViewPassB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabuuDrawingList.size() == 3 || tabuuDrawingList.size() == 1){
                    Snackbar.make(v,"Maalesef tabu kartınız bitti. Tabu kartı satın almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(v,"Tabuu kartlarınız başarılı bir şekilde satın alındı :)",Snackbar.LENGTH_LONG).show();
                                }
                            }).show();
                }else {
                    if (textViewPlayDrawingTeamA.getText().toString().equals(teamsList.get(0).getTeamAname())){
                        if (passCounterA<=0){
                            Toast.makeText(DrawingPlayActivity.this,"Pas hakkınız bitti",Toast.LENGTH_SHORT).show();
                        }else {
                            pauseTimer();
                            passCounterA--;
                            Toast.makeText(DrawingPlayActivity.this,passCounterA+" pas hakkınız kaldı",Toast.LENGTH_SHORT).show();

                            path.reset();

                            getRandomList(tabuuDrawingList);

                            getTabuuDrawingCard();

                        }
                    }else {
                        if (passCounterB<=0){
                            Toast.makeText(DrawingPlayActivity.this,"Pas hakkınız bitti",Toast.LENGTH_SHORT).show();
                        }else {
                            pauseTimer();
                            passCounterB--;
                            Toast.makeText(DrawingPlayActivity.this,passCounterB+" pas hakkınız kaldı",Toast.LENGTH_SHORT).show();

                            path.reset();

                            getRandomList(tabuuDrawingList);

                            getTabuuDrawingCard();

                        }
                    }

                }

            }
        });


        cardViewDrawingPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning){
                    pauseTimer();

                    if (!buyprocess){
                        if (rewardedAd.isLoaded()){
                            rewardedAd.show(DrawingPlayActivity.this,rewardedAdCallback);
                        }
                    }

                    constraintLayoutThings.setVisibility(View.INVISIBLE);
                    cardViewClear.setVisibility(View.INVISIBLE);
                    cardViewCorrectB.setVisibility(View.INVISIBLE);
                    cardViewPassB.setVisibility(View.INVISIBLE);

                }else {
                    startTimer();

                    constraintLayoutThings.setVisibility(View.VISIBLE);
                    cardViewClear.setVisibility(View.VISIBLE);
                    cardViewCorrectB.setVisibility(View.VISIBLE);
                    cardViewPassB.setVisibility(View.VISIBLE);
                }

            }
        });

        cardViewPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paletteVisibility++;
                switch (paletteVisibility%2){
                    case 0:
                        cardViewPen.setVisibility(View.INVISIBLE);
                        cardViewMagenta.setVisibility(View.INVISIBLE);
                        cardViewRed.setVisibility(View.INVISIBLE);
                        cardViewGreen.setVisibility(View.INVISIBLE);
                        cardViewBlue.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        cardViewPen.setVisibility(View.VISIBLE);
                        cardViewMagenta.setVisibility(View.VISIBLE);
                        cardViewRed.setVisibility(View.VISIBLE);
                        cardViewGreen.setVisibility(View.VISIBLE);
                        cardViewBlue.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });

    }

    private void getTabuuDrawingCard() {
        if (tabuuDrawingList.isEmpty()){
            Snackbar.make(view,"Maalesef tabu kartınız bitti. Tabu kartı satın almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                    .setAction("SATIN AL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(v,"Tabuu kartlarınız başarılı bir şekilde satın alındı :)",Snackbar.LENGTH_LONG).show();
                        }
                    }).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawingPlayActivity.this);
            builder.setCancelable(false);
            builder.setTitle("ANLATILACAK KELİME");
            builder.setMessage("Size verilen sürede '"+tabuuDrawingList.get(index).getWord()+"' anlatınız");
            builder.setPositiveButton("BAŞLA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (timerRunning){
                        pauseTimer();
                    }else {
                        startTimer();
                    }

                    tabuuDrawingList.remove(index);

                }
            });

            builder.create().show();
        }


    }

    private void getRandomList(List<TabuuDrawing> tabuuDrawingList) {
        index = random.nextInt(tabuuDrawingList.size());
    }

    private void addTabuuDrawingCard() {

        TabuuDrawing tabuuDrawing = new TabuuDrawing("BUZ");
        TabuuDrawing tabuuDrawing1 = new TabuuDrawing("ROKET");
        TabuuDrawing tabuuDrawing2 = new TabuuDrawing("ARKADAŞ");
        TabuuDrawing tabuuDrawing3 = new TabuuDrawing("TORNAVİDA");
        TabuuDrawing tabuuDrawing4 = new TabuuDrawing("MAKİNA");
        TabuuDrawing tabuuDrawing5 = new TabuuDrawing("TATİL");
        TabuuDrawing tabuuDrawing6 = new TabuuDrawing("KAR");
        TabuuDrawing tabuuDrawing7 = new TabuuDrawing("BOT (KIŞLIK AYAKKABI)");
        TabuuDrawing tabuuDrawing8 = new TabuuDrawing("TAKIM ELBİSE");
        TabuuDrawing tabuuDrawing9 = new TabuuDrawing("MODEM");
        TabuuDrawing tabuuDrawing10 = new TabuuDrawing("NAR");
        TabuuDrawing tabuuDrawing11 = new TabuuDrawing("ALTIN");
        TabuuDrawing tabuuDrawing12 = new TabuuDrawing("MAYMUN");
        TabuuDrawing tabuuDrawing13 = new TabuuDrawing("SIHA - IHA");
        TabuuDrawing tabuuDrawing14 = new TabuuDrawing("KELEBEK");
        TabuuDrawing tabuuDrawing15 = new TabuuDrawing("MARS");
        TabuuDrawing tabuuDrawing16 = new TabuuDrawing("FİLM");
        TabuuDrawing tabuuDrawing17 = new TabuuDrawing("DOKTOR");
        TabuuDrawing tabuuDrawing18 = new TabuuDrawing("OMURGA");
        TabuuDrawing tabuuDrawing19 = new TabuuDrawing("PATEN");
        TabuuDrawing tabuuDrawing20 = new TabuuDrawing("MANGAL");
        TabuuDrawing tabuuDrawing21 = new TabuuDrawing("HAMBURGER");
        TabuuDrawing tabuuDrawing22 = new TabuuDrawing("İSTANBUL");
        TabuuDrawing tabuuDrawing23 = new TabuuDrawing("VIDEO OYUNU");
        TabuuDrawing tabuuDrawing24 = new TabuuDrawing("ZABITA");
        TabuuDrawing tabuuDrawing25 = new TabuuDrawing("BASKETBOLCU");
        TabuuDrawing tabuuDrawing26 = new TabuuDrawing("YAZ MEVSİMİ");
        TabuuDrawing tabuuDrawing27 = new TabuuDrawing("TELEFON FLAŞI");
        TabuuDrawing tabuuDrawing28 = new TabuuDrawing("DAMAT");
        TabuuDrawing tabuuDrawing29 = new TabuuDrawing("TRAMVAY");
        TabuuDrawing tabuuDrawing30 = new TabuuDrawing("PAZAR");
        TabuuDrawing tabuuDrawing31 = new TabuuDrawing("DİŞ FIRÇASI");
        TabuuDrawing tabuuDrawing32 = new TabuuDrawing("KAMERA");
        TabuuDrawing tabuuDrawing33 = new TabuuDrawing("GİTAR");
        TabuuDrawing tabuuDrawing34 = new TabuuDrawing("TABUU OYUNU");
        TabuuDrawing tabuuDrawing35 = new TabuuDrawing("İBADET");

        //Mağaza için kullanılacak kartlar

        TabuuDrawing tabuuDrawing36 = new TabuuDrawing("DIŞKI");
        TabuuDrawing tabuuDrawing37 = new TabuuDrawing("GÖKKUŞAĞI");
        TabuuDrawing tabuuDrawing38 = new TabuuDrawing("YILAN");
        TabuuDrawing tabuuDrawing39 = new TabuuDrawing("MANKEN");
        TabuuDrawing tabuuDrawing40 = new TabuuDrawing("BAŞÖRTÜ");
        TabuuDrawing tabuuDrawing41 = new TabuuDrawing("KALE");
        TabuuDrawing tabuuDrawing42 = new TabuuDrawing("DENİZ");
        TabuuDrawing tabuuDrawing43 = new TabuuDrawing("AYI");
        TabuuDrawing tabuuDrawing44 = new TabuuDrawing("ETEK");
        TabuuDrawing tabuuDrawing45 = new TabuuDrawing("SAKAL");
        TabuuDrawing tabuuDrawing46 = new TabuuDrawing("MALA");
        TabuuDrawing tabuuDrawing47 = new TabuuDrawing("ATAÇ");
        TabuuDrawing tabuuDrawing48 = new TabuuDrawing("KİTAP");
        TabuuDrawing tabuuDrawing49 = new TabuuDrawing("SALATA");
        TabuuDrawing tabuuDrawing50 = new TabuuDrawing("KLAVYE");
        TabuuDrawing tabuuDrawing51 = new TabuuDrawing("DOLUNAY");
        TabuuDrawing tabuuDrawing52 = new TabuuDrawing("KURT");
        TabuuDrawing tabuuDrawing53 = new TabuuDrawing("HAPİSHANE");
        TabuuDrawing tabuuDrawing54 = new TabuuDrawing("EMZİK");
        TabuuDrawing tabuuDrawing55 = new TabuuDrawing("İNEK");
        TabuuDrawing tabuuDrawing56 = new TabuuDrawing("YAPRAK");
        TabuuDrawing tabuuDrawing57 = new TabuuDrawing("HUZUR EVİ");
        TabuuDrawing tabuuDrawing58 = new TabuuDrawing("HOPARLÖR");
        TabuuDrawing tabuuDrawing59 = new TabuuDrawing("DÜKKAN");
        TabuuDrawing tabuuDrawing60 = new TabuuDrawing("POKEMON");
        TabuuDrawing tabuuDrawing61 = new TabuuDrawing("TEPSİ");
        TabuuDrawing tabuuDrawing62 = new TabuuDrawing("MASA");
        TabuuDrawing tabuuDrawing63 = new TabuuDrawing("ZİHİN");
        TabuuDrawing tabuuDrawing64 = new TabuuDrawing("AKCİĞER");
        TabuuDrawing tabuuDrawing65 = new TabuuDrawing("UZAY");
        TabuuDrawing tabuuDrawing66 = new TabuuDrawing("KARAKTER");
        TabuuDrawing tabuuDrawing67 = new TabuuDrawing("POLİS");
        TabuuDrawing tabuuDrawing68 = new TabuuDrawing("ÖFKE");
        TabuuDrawing tabuuDrawing69 = new TabuuDrawing("GÜLMEK");
        TabuuDrawing tabuuDrawing70 = new TabuuDrawing("KALP");
        TabuuDrawing tabuuDrawing71 = new TabuuDrawing("ANTİBİYOTİK");
        TabuuDrawing tabuuDrawing72 = new TabuuDrawing("BİTKİ");
        TabuuDrawing tabuuDrawing73 = new TabuuDrawing("BÖCEK");
        TabuuDrawing tabuuDrawing74 = new TabuuDrawing("SPOR");
        TabuuDrawing tabuuDrawing75 = new TabuuDrawing("HALTER");
        TabuuDrawing tabuuDrawing76 = new TabuuDrawing("KAMYON");
        TabuuDrawing tabuuDrawing77 = new TabuuDrawing("TIR");
        TabuuDrawing tabuuDrawing78 = new TabuuDrawing("OKUL");
        TabuuDrawing tabuuDrawing79 = new TabuuDrawing("UÇAK");
        TabuuDrawing tabuuDrawing80 = new TabuuDrawing("HALI");
        TabuuDrawing tabuuDrawing81 = new TabuuDrawing("SOBA");
        TabuuDrawing tabuuDrawing82 = new TabuuDrawing("KALORİFER");
        TabuuDrawing tabuuDrawing83 = new TabuuDrawing("PEÇETE");
        TabuuDrawing tabuuDrawing84 = new TabuuDrawing("YASTIK");
        TabuuDrawing tabuuDrawing85 = new TabuuDrawing("TERMUS");

        //Mağaza için kullanılacak kartlar

        TabuuDrawing tabuuDrawing86 = new TabuuDrawing("KANGAL");
        TabuuDrawing tabuuDrawing87 = new TabuuDrawing("KÖPEK");
        TabuuDrawing tabuuDrawing88 = new TabuuDrawing("DELİ");
        TabuuDrawing tabuuDrawing89 = new TabuuDrawing("HAVALI İNSAN");
        TabuuDrawing tabuuDrawing90 = new TabuuDrawing("SİLAH");
        TabuuDrawing tabuuDrawing91 = new TabuuDrawing("BUZUL");
        TabuuDrawing tabuuDrawing92 = new TabuuDrawing("RUHSAT");
        TabuuDrawing tabuuDrawing93 = new TabuuDrawing("EVLİLİK CÜZDANI");
        TabuuDrawing tabuuDrawing94 = new TabuuDrawing("TÜNEL");
        TabuuDrawing tabuuDrawing95 = new TabuuDrawing("ENERJİ");
        TabuuDrawing tabuuDrawing96 = new TabuuDrawing("SEMAVER");
        TabuuDrawing tabuuDrawing97 = new TabuuDrawing("TERMOS");
        TabuuDrawing tabuuDrawing98 = new TabuuDrawing("PASTA");
        TabuuDrawing tabuuDrawing99 = new TabuuDrawing("DENİZ YILDIZI");
        TabuuDrawing tabuuDrawing100 = new TabuuDrawing("SİVİLCE");
        TabuuDrawing tabuuDrawing101 = new TabuuDrawing("KAVGA ETMEK");
        TabuuDrawing tabuuDrawing102 = new TabuuDrawing("OTOBAN");
        TabuuDrawing tabuuDrawing103 = new TabuuDrawing("PANSUMAN");
        TabuuDrawing tabuuDrawing104 = new TabuuDrawing("KAN");
        TabuuDrawing tabuuDrawing105 = new TabuuDrawing("ARABA ÇARPMAK");
        TabuuDrawing tabuuDrawing106 = new TabuuDrawing("TRAFİK");
        TabuuDrawing tabuuDrawing107 = new TabuuDrawing("HALTER");
        TabuuDrawing tabuuDrawing108 = new TabuuDrawing("KAPSÜL");
        TabuuDrawing tabuuDrawing109 = new TabuuDrawing("ARI");
        TabuuDrawing tabuuDrawing110 = new TabuuDrawing("FABRİKA");
        TabuuDrawing tabuuDrawing111 = new TabuuDrawing("RADYASYON");
        TabuuDrawing tabuuDrawing112 = new TabuuDrawing("DÜKKAN");
        TabuuDrawing tabuuDrawing113 = new TabuuDrawing("SOBA");
        TabuuDrawing tabuuDrawing114 = new TabuuDrawing("SOSYAL MEDYA");
        TabuuDrawing tabuuDrawing115 = new TabuuDrawing("KUM SAATİ");
        TabuuDrawing tabuuDrawing116 = new TabuuDrawing("AĞDA YAPTIRMAK");
        TabuuDrawing tabuuDrawing117 = new TabuuDrawing("KARAKOL");
        TabuuDrawing tabuuDrawing118 = new TabuuDrawing("HAPİS");
        TabuuDrawing tabuuDrawing119 = new TabuuDrawing("SPOR YAPMAK");
        TabuuDrawing tabuuDrawing120 = new TabuuDrawing("YAKA");
        TabuuDrawing tabuuDrawing121 = new TabuuDrawing("GRAVAT");
        TabuuDrawing tabuuDrawing122 = new TabuuDrawing("VIRUS");
        TabuuDrawing tabuuDrawing123 = new TabuuDrawing("KEDİ");
        TabuuDrawing tabuuDrawing124 = new TabuuDrawing("DİN");
        TabuuDrawing tabuuDrawing125 = new TabuuDrawing("ÖDEMEK");
        TabuuDrawing tabuuDrawing126 = new TabuuDrawing("YOKUŞ");
        TabuuDrawing tabuuDrawing127 = new TabuuDrawing("DOLAR");
        TabuuDrawing tabuuDrawing128 = new TabuuDrawing("ARAP İNSANI");
        TabuuDrawing tabuuDrawing129 = new TabuuDrawing("KABE");
        TabuuDrawing tabuuDrawing130 = new TabuuDrawing("KAN GRUBU");
        TabuuDrawing tabuuDrawing131 = new TabuuDrawing("PASAPORT");
        TabuuDrawing tabuuDrawing132 = new TabuuDrawing("KANCA");
        TabuuDrawing tabuuDrawing133 = new TabuuDrawing("YÜZ");
        TabuuDrawing tabuuDrawing134 = new TabuuDrawing("EL YAZISI");
        TabuuDrawing tabuuDrawing135 = new TabuuDrawing("PATEN");
        TabuuDrawing tabuuDrawing136 = new TabuuDrawing("KAYMAK");
        TabuuDrawing tabuuDrawing137 = new TabuuDrawing("DAĞ");
        TabuuDrawing tabuuDrawing138 = new TabuuDrawing("ÇIĞ");
        TabuuDrawing tabuuDrawing139 = new TabuuDrawing("ALIŞVERİŞ");
        TabuuDrawing tabuuDrawing140 = new TabuuDrawing("MEZARLIK");
        TabuuDrawing tabuuDrawing141 = new TabuuDrawing("CENAZE");
        TabuuDrawing tabuuDrawing142 = new TabuuDrawing("KAMYON");
        TabuuDrawing tabuuDrawing143 = new TabuuDrawing("YAMAÇ");
        TabuuDrawing tabuuDrawing144 = new TabuuDrawing("KIYI");
        TabuuDrawing tabuuDrawing145 = new TabuuDrawing("SOĞAN");
        TabuuDrawing tabuuDrawing146 = new TabuuDrawing("TANRI");
        TabuuDrawing tabuuDrawing147 = new TabuuDrawing("KARNE");
        TabuuDrawing tabuuDrawing148 = new TabuuDrawing("DERS ÇALIŞMAK");
        TabuuDrawing tabuuDrawing149 = new TabuuDrawing("PRİZ");
        TabuuDrawing tabuuDrawing150 = new TabuuDrawing("MAĞARA");
        TabuuDrawing tabuuDrawing151 = new TabuuDrawing("PİL");
        TabuuDrawing tabuuDrawing152 = new TabuuDrawing("SABUN");
        TabuuDrawing tabuuDrawing153 = new TabuuDrawing("DEZENFEKTAN");
        TabuuDrawing tabuuDrawing154 = new TabuuDrawing("PARANTEZ");
        TabuuDrawing tabuuDrawing155 = new TabuuDrawing("PROGRAMLAMA");
        TabuuDrawing tabuuDrawing156 = new TabuuDrawing("YERALTI");
        TabuuDrawing tabuuDrawing157 = new TabuuDrawing("VAGON");
        TabuuDrawing tabuuDrawing158 = new TabuuDrawing("SAZ");
        TabuuDrawing tabuuDrawing159 = new TabuuDrawing("GİTAR");
        TabuuDrawing tabuuDrawing160 = new TabuuDrawing("ALIMLI");
        TabuuDrawing tabuuDrawing161 = new TabuuDrawing("PANZER");
        TabuuDrawing tabuuDrawing162 = new TabuuDrawing("FOBİ");
        TabuuDrawing tabuuDrawing163 = new TabuuDrawing("TELVE");
        TabuuDrawing tabuuDrawing164 = new TabuuDrawing("BUZUL");
        TabuuDrawing tabuuDrawing165 = new TabuuDrawing("GÖZLÜK");
        TabuuDrawing tabuuDrawing166 = new TabuuDrawing("OPTİK");
        TabuuDrawing tabuuDrawing167 = new TabuuDrawing("KIŞ");
        TabuuDrawing tabuuDrawing168 = new TabuuDrawing("YAZ");
        TabuuDrawing tabuuDrawing169 = new TabuuDrawing("SEYEHAT");
        TabuuDrawing tabuuDrawing170 = new TabuuDrawing("HAMAM");
        TabuuDrawing tabuuDrawing171 = new TabuuDrawing("BANDANA");
        TabuuDrawing tabuuDrawing172 = new TabuuDrawing("ÖRÜMCEK");
        TabuuDrawing tabuuDrawing173 = new TabuuDrawing("ADA");
        TabuuDrawing tabuuDrawing174 = new TabuuDrawing("KOLTUK TAKIMI");
        TabuuDrawing tabuuDrawing175 = new TabuuDrawing("SATÜRN");
        TabuuDrawing tabuuDrawing176 = new TabuuDrawing("ÇAMUR");
        TabuuDrawing tabuuDrawing177 = new TabuuDrawing("ZAYIF");
        TabuuDrawing tabuuDrawing178 = new TabuuDrawing("KİLO");
        TabuuDrawing tabuuDrawing179 = new TabuuDrawing("ÇORBA");
        TabuuDrawing tabuuDrawing180 = new TabuuDrawing("İZMİR");
        TabuuDrawing tabuuDrawing181 = new TabuuDrawing("METEOROLOJİ");
        TabuuDrawing tabuuDrawing182 = new TabuuDrawing("TIRNAK");
        TabuuDrawing tabuuDrawing183 = new TabuuDrawing("YÜZMEK");
        TabuuDrawing tabuuDrawing184 = new TabuuDrawing("PASTA");
        TabuuDrawing tabuuDrawing185 = new TabuuDrawing("PENCERE");
        TabuuDrawing tabuuDrawing186 = new TabuuDrawing("ÇAYDANLIK");



        tabuuDrawingList.add(tabuuDrawing);
        tabuuDrawingList.add(tabuuDrawing1);
        tabuuDrawingList.add(tabuuDrawing2);
        tabuuDrawingList.add(tabuuDrawing3);
        tabuuDrawingList.add(tabuuDrawing4);
        tabuuDrawingList.add(tabuuDrawing5);
        tabuuDrawingList.add(tabuuDrawing6);
        tabuuDrawingList.add(tabuuDrawing7);
        tabuuDrawingList.add(tabuuDrawing8);
        tabuuDrawingList.add(tabuuDrawing9);
        tabuuDrawingList.add(tabuuDrawing10);
        tabuuDrawingList.add(tabuuDrawing11);
        tabuuDrawingList.add(tabuuDrawing12);
        tabuuDrawingList.add(tabuuDrawing13);
        tabuuDrawingList.add(tabuuDrawing14);
        tabuuDrawingList.add(tabuuDrawing15);
        tabuuDrawingList.add(tabuuDrawing16);
        tabuuDrawingList.add(tabuuDrawing17);
        tabuuDrawingList.add(tabuuDrawing18);
        tabuuDrawingList.add(tabuuDrawing19);
        tabuuDrawingList.add(tabuuDrawing20);
        tabuuDrawingList.add(tabuuDrawing21);
        tabuuDrawingList.add(tabuuDrawing22);
        tabuuDrawingList.add(tabuuDrawing23);
        tabuuDrawingList.add(tabuuDrawing24);
        tabuuDrawingList.add(tabuuDrawing25);
        tabuuDrawingList.add(tabuuDrawing26);
        tabuuDrawingList.add(tabuuDrawing27);
        tabuuDrawingList.add(tabuuDrawing28);
        tabuuDrawingList.add(tabuuDrawing29);
        tabuuDrawingList.add(tabuuDrawing30);
        tabuuDrawingList.add(tabuuDrawing31);
        tabuuDrawingList.add(tabuuDrawing32);
        tabuuDrawingList.add(tabuuDrawing33);
        tabuuDrawingList.add(tabuuDrawing34);
        tabuuDrawingList.add(tabuuDrawing35);

        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
        boolean buyprocess = sharedPreferences.getBoolean("buy50drawingtabuu",false);

        if (buyprocess){
            tabuuDrawingList.add(tabuuDrawing36);
            tabuuDrawingList.add(tabuuDrawing37);
            tabuuDrawingList.add(tabuuDrawing38);
            tabuuDrawingList.add(tabuuDrawing39);
            tabuuDrawingList.add(tabuuDrawing40);
            tabuuDrawingList.add(tabuuDrawing41);
            tabuuDrawingList.add(tabuuDrawing42);
            tabuuDrawingList.add(tabuuDrawing43);
            tabuuDrawingList.add(tabuuDrawing44);
            tabuuDrawingList.add(tabuuDrawing45);
            tabuuDrawingList.add(tabuuDrawing46);
            tabuuDrawingList.add(tabuuDrawing47);
            tabuuDrawingList.add(tabuuDrawing48);
            tabuuDrawingList.add(tabuuDrawing49);
            tabuuDrawingList.add(tabuuDrawing50);
            tabuuDrawingList.add(tabuuDrawing51);
            tabuuDrawingList.add(tabuuDrawing52);
            tabuuDrawingList.add(tabuuDrawing53);
            tabuuDrawingList.add(tabuuDrawing54);
            tabuuDrawingList.add(tabuuDrawing55);
            tabuuDrawingList.add(tabuuDrawing56);
            tabuuDrawingList.add(tabuuDrawing57);
            tabuuDrawingList.add(tabuuDrawing58);
            tabuuDrawingList.add(tabuuDrawing59);
            tabuuDrawingList.add(tabuuDrawing60);
            tabuuDrawingList.add(tabuuDrawing61);
            tabuuDrawingList.add(tabuuDrawing62);
            tabuuDrawingList.add(tabuuDrawing63);
            tabuuDrawingList.add(tabuuDrawing64);
            tabuuDrawingList.add(tabuuDrawing65);
            tabuuDrawingList.add(tabuuDrawing66);
            tabuuDrawingList.add(tabuuDrawing67);
            tabuuDrawingList.add(tabuuDrawing68);
            tabuuDrawingList.add(tabuuDrawing69);
            tabuuDrawingList.add(tabuuDrawing70);
            tabuuDrawingList.add(tabuuDrawing71);
            tabuuDrawingList.add(tabuuDrawing72);
            tabuuDrawingList.add(tabuuDrawing73);
            tabuuDrawingList.add(tabuuDrawing74);
            tabuuDrawingList.add(tabuuDrawing75);
            tabuuDrawingList.add(tabuuDrawing76);
            tabuuDrawingList.add(tabuuDrawing77);
            tabuuDrawingList.add(tabuuDrawing78);
            tabuuDrawingList.add(tabuuDrawing79);
            tabuuDrawingList.add(tabuuDrawing80);
            tabuuDrawingList.add(tabuuDrawing81);
            tabuuDrawingList.add(tabuuDrawing82);
            tabuuDrawingList.add(tabuuDrawing83);
            tabuuDrawingList.add(tabuuDrawing84);
            tabuuDrawingList.add(tabuuDrawing85);
        }


        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
        boolean buyprocess2 = sharedPreferences.getBoolean("buy100drawingtabuu",false);


        if (buyprocess2){
            tabuuDrawingList.add(tabuuDrawing86);
            tabuuDrawingList.add(tabuuDrawing87);
            tabuuDrawingList.add(tabuuDrawing88);
            tabuuDrawingList.add(tabuuDrawing89);
            tabuuDrawingList.add(tabuuDrawing90);
            tabuuDrawingList.add(tabuuDrawing91);
            tabuuDrawingList.add(tabuuDrawing92);
            tabuuDrawingList.add(tabuuDrawing93);
            tabuuDrawingList.add(tabuuDrawing94);
            tabuuDrawingList.add(tabuuDrawing95);
            tabuuDrawingList.add(tabuuDrawing96);
            tabuuDrawingList.add(tabuuDrawing97);
            tabuuDrawingList.add(tabuuDrawing98);
            tabuuDrawingList.add(tabuuDrawing99);
            tabuuDrawingList.add(tabuuDrawing100);
            tabuuDrawingList.add(tabuuDrawing101);
            tabuuDrawingList.add(tabuuDrawing102);
            tabuuDrawingList.add(tabuuDrawing103);
            tabuuDrawingList.add(tabuuDrawing104);
            tabuuDrawingList.add(tabuuDrawing105);
            tabuuDrawingList.add(tabuuDrawing106);
            tabuuDrawingList.add(tabuuDrawing107);
            tabuuDrawingList.add(tabuuDrawing108);
            tabuuDrawingList.add(tabuuDrawing109);
            tabuuDrawingList.add(tabuuDrawing110);
            tabuuDrawingList.add(tabuuDrawing111);
            tabuuDrawingList.add(tabuuDrawing112);
            tabuuDrawingList.add(tabuuDrawing113);
            tabuuDrawingList.add(tabuuDrawing114);
            tabuuDrawingList.add(tabuuDrawing115);
            tabuuDrawingList.add(tabuuDrawing116);
            tabuuDrawingList.add(tabuuDrawing117);
            tabuuDrawingList.add(tabuuDrawing118);
            tabuuDrawingList.add(tabuuDrawing119);
            tabuuDrawingList.add(tabuuDrawing120);
            tabuuDrawingList.add(tabuuDrawing121);
            tabuuDrawingList.add(tabuuDrawing122);
            tabuuDrawingList.add(tabuuDrawing123);
            tabuuDrawingList.add(tabuuDrawing124);
            tabuuDrawingList.add(tabuuDrawing125);
            tabuuDrawingList.add(tabuuDrawing126);
            tabuuDrawingList.add(tabuuDrawing127);
            tabuuDrawingList.add(tabuuDrawing128);
            tabuuDrawingList.add(tabuuDrawing129);
            tabuuDrawingList.add(tabuuDrawing130);
            tabuuDrawingList.add(tabuuDrawing131);
            tabuuDrawingList.add(tabuuDrawing132);
            tabuuDrawingList.add(tabuuDrawing133);
            tabuuDrawingList.add(tabuuDrawing134);
            tabuuDrawingList.add(tabuuDrawing135);
            tabuuDrawingList.add(tabuuDrawing136);
            tabuuDrawingList.add(tabuuDrawing137);
            tabuuDrawingList.add(tabuuDrawing138);
            tabuuDrawingList.add(tabuuDrawing139);
            tabuuDrawingList.add(tabuuDrawing140);
            tabuuDrawingList.add(tabuuDrawing141);
            tabuuDrawingList.add(tabuuDrawing142);
            tabuuDrawingList.add(tabuuDrawing143);
            tabuuDrawingList.add(tabuuDrawing144);
            tabuuDrawingList.add(tabuuDrawing145);
            tabuuDrawingList.add(tabuuDrawing146);
            tabuuDrawingList.add(tabuuDrawing147);
            tabuuDrawingList.add(tabuuDrawing148);
            tabuuDrawingList.add(tabuuDrawing149);
            tabuuDrawingList.add(tabuuDrawing150);
            tabuuDrawingList.add(tabuuDrawing151);
            tabuuDrawingList.add(tabuuDrawing152);
            tabuuDrawingList.add(tabuuDrawing153);
            tabuuDrawingList.add(tabuuDrawing154);
            tabuuDrawingList.add(tabuuDrawing155);
            tabuuDrawingList.add(tabuuDrawing156);
            tabuuDrawingList.add(tabuuDrawing157);
            tabuuDrawingList.add(tabuuDrawing158);
            tabuuDrawingList.add(tabuuDrawing159);
            tabuuDrawingList.add(tabuuDrawing160);
            tabuuDrawingList.add(tabuuDrawing161);
            tabuuDrawingList.add(tabuuDrawing162);
            tabuuDrawingList.add(tabuuDrawing163);
            tabuuDrawingList.add(tabuuDrawing164);
            tabuuDrawingList.add(tabuuDrawing165);
            tabuuDrawingList.add(tabuuDrawing166);
            tabuuDrawingList.add(tabuuDrawing167);
            tabuuDrawingList.add(tabuuDrawing168);
            tabuuDrawingList.add(tabuuDrawing169);
            tabuuDrawingList.add(tabuuDrawing170);
            tabuuDrawingList.add(tabuuDrawing171);
            tabuuDrawingList.add(tabuuDrawing172);
            tabuuDrawingList.add(tabuuDrawing173);
            tabuuDrawingList.add(tabuuDrawing174);
            tabuuDrawingList.add(tabuuDrawing175);
            tabuuDrawingList.add(tabuuDrawing176);
            tabuuDrawingList.add(tabuuDrawing177);
            tabuuDrawingList.add(tabuuDrawing178);
            tabuuDrawingList.add(tabuuDrawing179);
            tabuuDrawingList.add(tabuuDrawing180);
            tabuuDrawingList.add(tabuuDrawing181);
            tabuuDrawingList.add(tabuuDrawing182);
            tabuuDrawingList.add(tabuuDrawing183);
            tabuuDrawingList.add(tabuuDrawing184);
            tabuuDrawingList.add(tabuuDrawing185);
            tabuuDrawingList.add(tabuuDrawing186);
        }


    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LEFT_IN_MILLIS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT_IN_MILLIS = millisUntilFinished;
                int secondary = Integer.parseInt(String.valueOf(TIME_LEFT_IN_MILLIS/1000));
                progressBarDrawingPlay.setProgress(secondary);
                updateCountDownText();

                if (textViewPlaySecondsDrawingCounter.getText().toString().equals("00:00") && textViewPlayDrawingTeamA.getText().toString().equals(teamsList.get(0).getTeamBname())){
                    /*Score score = new Score(scoreList.get(0).getTeamAscore(),scoreList.get(0).getTeamBscore());
                    scoreList.add(score);*/

                    path.reset();


                    ScoreB scoreB = new ScoreB(correctCounter);
                    scoreBList.clear();
                    scoreBList.add(scoreB);

                    showAlertDialogScores();

                    //Toast.makeText(ClassicPlayActivity.this,"Bitti",Toast.LENGTH_SHORT).show();
                }

                if (textViewPlaySecondsDrawingCounter.getText().toString().equals("00:00") && textViewPlayDrawingTeamA.getText().toString().equals(teamsList.get(0).getTeamAname())){
                    /*Score score = new Score(correctCounter,0);
                    scoreList.clear();
                    scoreList.add(score);*/

                    path.reset();


                    ScoreA scoreA = new ScoreA(correctCounter);
                    scoreAList.clear();
                    scoreAList.add(scoreA);

                    ScoreB scoreB = new ScoreB(0);
                    scoreBList.clear();
                    scoreBList.add(scoreB);

                    showAlertDialogScores();

                    //Toast.makeText(ClassicPlayActivity.this,"Bitti",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFinish() {
                timerRunning = false;

            }
        }.start();

        timerRunning = true;

    }

    private void showAlertDialogScores() {
        if (textViewPlayDrawingTeamA.getText().toString().equals(teamsList.get(0).getTeamBname())){
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawingPlayActivity.this);
            builder.setCancelable(false);
            builder.setTitle("SKORLAR");
            builder.setMessage(teamsList.get(0).getTeamAname()+": "+scoreAList.get(0).getScoreA()+"\n"+teamsList.get(0).getTeamBname()+": "+scoreBList.get(0).getScoreB());
            builder.setPositiveButton("YENİ OYUN OYNA", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
                    boolean buyprocess = sharedPreferences.getBoolean("buynoads",false);

                    if (!buyprocess){
                        if (rewardedAd.isLoaded()){
                            rewardedAd.show(DrawingPlayActivity.this,rewardedAdCallback);
                        }
                    }

                    passCounterA = bothOfTeams;
                    passCounterB = bothOfTeams;
                    textViewPlayDrawingTeamA.setText(teamsList.get(0).getTeamAname());



                    scoreAList.clear();
                    scoreBList.clear();
                    correctCounter = 0;
                    textViewPlayScore.setText("SKOR: "+correctCounter);
                    //textViewPlayClassicTeamA.setText(teamsList.get(0).getTeamBname());
                    resetTimer();
                    //startTimer();

                    getTabuuDrawingCard();

                }
            });

            builder.create().show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawingPlayActivity.this);
            builder.setCancelable(false);
            builder.setTitle("SKORLAR");
            builder.setMessage(teamsList.get(0).getTeamAname()+": "+scoreAList.get(0).getScoreA()+"\n"+teamsList.get(0).getTeamBname()+": "+scoreBList.get(0).getScoreB());
            builder.setPositiveButton("DEVAM", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //scoreList.clear();
                    correctCounter = 0;
                    textViewPlayScore.setText("SKOR: "+correctCounter);
                    textViewPlayDrawingTeamA.setText(teamsList.get(0).getTeamBname());
                    resetTimer();
                    //startTimer();

                    getTabuuDrawingCard();

                }
            });

            builder.create().show();
        }


    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void resetTimer(){
        TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        updateCountDownText();

    }

    private void updateCountDownText() {
        int minutes = (int) (TIME_LEFT_IN_MILLIS / 1000) / 60;
        int seconds = (int) (TIME_LEFT_IN_MILLIS) / 1000 % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        textViewPlaySecondsDrawingCounter.setText(timeLeftFormatted);
    }


    public void pencil(View view){
        paint_brush.setColor(Color.BLACK);
        currentColor(paint_brush.getColor());

    }

    public void eraser(View view){
        Display.pathArrayList.clear();
        Display.colorArraylist.clear();
        path.reset();


    }

    public void redColor(View view){
        paint_brush.setColor(Color.RED);
        currentColor(paint_brush.getColor());

    }

    public void mangentaColor(View view){
        paint_brush.setColor(Color.MAGENTA);
        currentColor(paint_brush.getColor());

    }

    public void greenColor(View view){
        paint_brush.setColor(Color.GREEN);
        currentColor(paint_brush.getColor());

    }

    public void blueColor(View view){
        paint_brush.setColor(Color.BLUE);
        currentColor(paint_brush.getColor());

    }

    public void currentColor(int c){
        Display.current_brush = c;
        path = new Path();

    }


    public void penWidth(View view){
        PopupMenu popupMenu = new PopupMenu(DrawingPlayActivity.this,view);
        popupMenu.getMenuInflater().inflate(R.menu.pen_width_menu,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_three:
                        paint_brush.setStrokeWidth(3f);
                        return true;
                    case R.id.action_five:
                        paint_brush.setStrokeWidth(5f);
                        return true;
                    case R.id.action_eight:
                        paint_brush.setStrokeWidth(8f);
                        return true;
                    default:
                        return false;
                }

            }
        });

        popupMenu.show();
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawingPlayActivity.this);
        builder.setTitle("ANLAT BAKALIM");
        builder.setMessage("Oyundan çıkmak istediğinize emin misiniz ?");
        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //sonradan yazdığım kodlar 14/01/2021
                countDownTimer.cancel();
                teamsList.clear();
                scoreAList.clear();
                scoreBList.clear();

                path.reset(); // Evet'e tıklanınca ekranı silecek. Bu kodu yazmayınca geçmiş çizilen kaydediliyordu.
                //countDownTimer.cancel();
                Intent intent = new Intent(DrawingPlayActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
            for (Purchase purchase:list){
                if (!purchase.isAcknowledged()){
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken()).build();
                }

                if (purchase.getSku().equals("50drawingtabuu")){

                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (firebaseUser != null){
                        String uid = firebaseUser.getUid();

                        firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference("purchases50").child(firebaseUser.getUid());
                        com.furkanayaz.anlatbakalimcizbakalim.Purchase purchases50 = new com.furkanayaz.anlatbakalimcizbakalim.Purchase(uid);
                        databaseReference.push().setValue(purchases50);


                        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("50drawingtabuu",true);
                        editor.commit();
                    }else {
                        Toast.makeText(DrawingPlayActivity.this,"Lütfen satın alma işleminizi oyuna üye olarak gerçekleştiriniz",Toast.LENGTH_LONG).show();
                    }


                }

            }
        }

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawingPlayActivity.this);
            builder.setTitle("SATIN ALMA İŞLEMİ");
            builder.setMessage("Satın alma işlemi iptal edildi");
            builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();
        }

    }
}
package com.furkanayaz.anlatbakalimcizbakalim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class ClassicPlayActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private BillingClient billingClient;
    private List<SkuDetails> skuINAPPDetailList = new ArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RewardedAd rewardedAd;
    private RewardedAdLoadCallback rewardedAdLoadCallback;
    private RewardedAdCallback rewardedAdCallback;
    private TextView textViewPlayClassicTeamA;
    private TextView textViewPlaySecondsCounter;
    private TextView textViewPlayTabooWord,textViewPlayTaboo1,textViewPlayTaboo2,textViewPlayTaboo3,textViewPlayTaboo4,textViewPlayTaboo5;
    private TextView textViewPlayScore;
    private CardView cardViewPlayPause;
    private CardView cardViewTabuu,cardViewCorrect,cardViewPass;
    private CardView cardView,cardView1,cardView2,cardView3,cardView4,cardView5;
    private ProgressBar progressBarPlayA;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    

    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long START_TIME_IN_MILLIS = 0;
    private long TIME_LEFT_IN_MILLIS = 0;

    private List<Teams> teamsList = new ArrayList<>();
    private List<ScoreA> scoreAList = new ArrayList<>();
    private List<ScoreB> scoreBList = new ArrayList<>();


    private List<Tabuu> tabuuList = new ArrayList<>();
    private Random random = new Random();

    int seconds = 0;

    int correctCounter = 0;
    int passBothOfTeams = 0;
    int passCounterA = 0;
    int passCounterB = 0;
    int tabuuCounter = 0;

    int scorematchcounter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_play);

        textViewPlaySecondsCounter = findViewById(R.id.textViewPlaySecondsCounter);
        textViewPlayTabooWord = findViewById(R.id.textViewPlayTabooWord);
        textViewPlayTaboo1 = findViewById(R.id.textViewPlayTaboo1);
        textViewPlayTaboo2 = findViewById(R.id.textViewPlayTaboo2);
        textViewPlayTaboo3 = findViewById(R.id.textViewPlayTaboo3);
        textViewPlayTaboo4 = findViewById(R.id.textViewPlayTaboo4);
        textViewPlayTaboo5 = findViewById(R.id.textViewPlayTaboo5);

        textViewPlayClassicTeamA = findViewById(R.id.textViewPlayClassicTeamA);

        textViewPlayScore = findViewById(R.id.textViewPlayScore);

        cardViewPlayPause = findViewById(R.id.cardViewPlayPause);

        cardViewTabuu = findViewById(R.id.cardViewTabuu);
        cardViewCorrect = findViewById(R.id.cardViewCorrectB);
        cardViewPass = findViewById(R.id.cardViewPassB);

        cardView = findViewById(R.id.cardViewPen);
        cardView1 = findViewById(R.id.cardView3);
        cardView2 = findViewById(R.id.cardView4);
        cardView3 = findViewById(R.id.cardView5);
        cardView4 = findViewById(R.id.cardView6);
        cardView5 = findViewById(R.id.cardView1);

        progressBarPlayA = findViewById(R.id.progressBarPlayA);



        billingClient = BillingClient.newBuilder(ClassicPlayActivity.this).enablePendingPurchases().setListener(this).build();

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
                    Toast.makeText(ClassicPlayActivity.this,"Ödeme sistemi için Google Play hesabınızı kontrol ediniz",Toast.LENGTH_LONG).show();
                    //cardViewStatusChanger(false);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(ClassicPlayActivity.this,"Ödeme işlemi sağlanamadı",Toast.LENGTH_LONG).show();
                //cardViewStatusChanger(false);

            }
        });



        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
        boolean buyprocess = sharedPreferences.getBoolean("buynoads",false);

        //i have: ca-app-pub-5793841848623320/5294731118
        //test: ca-app-pub-3940256099942544/5224354917

        if (!buyprocess){
            MobileAds.initialize(ClassicPlayActivity.this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });

            rewardedAd = new RewardedAd(ClassicPlayActivity.this,"ca-app-pub-5793841848623320/5294731118");
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
                    rewardedAd = new RewardedAd(ClassicPlayActivity.this,"ca-app-pub-5793841848623320/5294731118");
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





        sharedPreferences = getSharedPreferences("TeamClassicNames",MODE_PRIVATE);
        String teamA = sharedPreferences.getString("teamClassicA","A TAKIMI");
        String teamB = sharedPreferences.getString("teamClassicB","B TAKIMI");
        Teams teams = new Teams(teamA,teamB);
        teamsList.add(teams);
        textViewPlayClassicTeamA.setText(teamsList.get(0).getTeamAname());

        sharedPreferences = getSharedPreferences("ClassicSettings",MODE_PRIVATE);

        seconds = sharedPreferences.getInt("classicseconds",180);
        int pass = sharedPreferences.getInt("classicpass",5);
        int tabuu = sharedPreferences.getInt("classictaboo",3);


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


        progressBarPlayA.setMax(seconds);


        passCounterA = pass; // passcounter ile azaltma işlemi yapıyorum.
        //tabuuCounter = tabuu; // tabuuCounter'ı hala kullanıyorum ve butona kaç defa tıklanıldığını öğreniyorum. tabuu ise correctNumber'dan çıkarma işlemini yapıyor.
        passCounterB = pass;

        passBothOfTeams = pass;



        addTabuuCard();
        getRandomList(tabuuList);



        if (timerRunning){
            pauseTimer();
        }else {
            startTimer();
        }


        cardViewPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerRunning){
                    pauseTimer();

                    if (!buyprocess){
                        if (rewardedAd.isLoaded()){
                            rewardedAd.show(ClassicPlayActivity.this,rewardedAdCallback);
                        }
                    }



                    cardViewTabuu.setVisibility(View.INVISIBLE);
                    cardViewCorrect.setVisibility(View.INVISIBLE);
                    cardViewPass.setVisibility(View.INVISIBLE);

                    cardView.setVisibility(View.INVISIBLE);
                    cardView1.setVisibility(View.INVISIBLE);
                    cardView2.setVisibility(View.INVISIBLE);
                    cardView3.setVisibility(View.INVISIBLE);
                    cardView4.setVisibility(View.INVISIBLE);
                    cardView5.setVisibility(View.INVISIBLE);


                }else {
                    startTimer();

                    cardViewTabuu.setVisibility(View.VISIBLE);
                    cardViewCorrect.setVisibility(View.VISIBLE);
                    cardViewPass.setVisibility(View.VISIBLE);

                    cardView.setVisibility(View.VISIBLE);
                    cardView1.setVisibility(View.VISIBLE);
                    cardView2.setVisibility(View.VISIBLE);
                    cardView3.setVisibility(View.VISIBLE);
                    cardView4.setVisibility(View.VISIBLE);
                    cardView5.setVisibility(View.VISIBLE);

                }

            }
        });


        updateCountDownText();


        cardViewCorrect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (tabuuList.isEmpty()){
                    Snackbar.make(v,"Maalesef tabu kartınız bitti. Tabu kartı satın almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (firebaseUser != null){

                                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuINAPPDetailList.get(1)).build();

                                        billingClient.launchBillingFlow(ClassicPlayActivity.this,flowParams);






                                        Snackbar.make(v,"Tabuu kartlarınız başarılı bir şekilde satın alındı :)",Snackbar.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(ClassicPlayActivity.this,"Satın alımlarınızı uygulamaya üye olarak gerçekleştiriniz",Toast.LENGTH_LONG).show();
                                    }


                                }
                            }).show();
                }else {
                    correctCounter++;
                    textViewPlayScore.setText("SKOR: "+correctCounter);
                    getRandomList(tabuuList);
                }


            }
        });

        cardViewPass.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (tabuuList.isEmpty()){
                    Snackbar.make(v,"Maalesef tabu kartınız bitti. Tabu kartı satın almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(v,"Tabuu kartlarınız başarılı bir şekilde satın alındı :)",Snackbar.LENGTH_LONG).show();
                                }
                            }).show();
                }else {
                    if (textViewPlayClassicTeamA.getText().toString().equals(teamsList.get(0).getTeamAname())){
                        if (passCounterA<=0){
                            Toast.makeText(ClassicPlayActivity.this,"Pas hakkınız bitti",Toast.LENGTH_SHORT).show();
                        }else {
                            passCounterA--;
                            Toast.makeText(ClassicPlayActivity.this,passCounterA+" pas hakkınız kaldı",Toast.LENGTH_SHORT).show();
                            getRandomList(tabuuList);
                        }
                    }else {
                        if (passCounterB<=0){
                            Toast.makeText(ClassicPlayActivity.this,"Pas hakkınız bitti",Toast.LENGTH_SHORT).show();
                        }else {
                            passCounterB--;
                            Toast.makeText(ClassicPlayActivity.this,passCounterB+" pas hakkınız kaldı",Toast.LENGTH_SHORT).show();
                            getRandomList(tabuuList);
                        }
                    }



                }


            }
        });

        cardViewTabuu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (tabuuList.isEmpty()){
                    Snackbar.make(v,"Maalesef tabu kartınız bitti. Tabu kartı satın almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(v,"Tabuu kartlarınız başarılı bir şekilde satın alındı :)",Snackbar.LENGTH_LONG).show();
                                }
                            }).show();
                }else {
                    tabuuCounter++;
                    correctCounter = correctCounter - tabuu;
                    textViewPlayScore.setText("SKOR: "+correctCounter);
                    getRandomList(tabuuList);

                }

            }
        });

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LEFT_IN_MILLIS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT_IN_MILLIS = millisUntilFinished;
                int secondary = Integer.parseInt(String.valueOf(TIME_LEFT_IN_MILLIS/1000));
                progressBarPlayA.setProgress(secondary);
                updateCountDownText();

                if (textViewPlaySecondsCounter.getText().toString().equals("00:00") && textViewPlayClassicTeamA.getText().toString().equals(teamsList.get(0).getTeamBname())){
                    /*Score score = new Score(scoreList.get(0).getTeamAscore(),scoreList.get(0).getTeamBscore());
                    scoreList.add(score);*/

                    ScoreB scoreB = new ScoreB(correctCounter);
                    scoreBList.clear();
                    scoreBList.add(scoreB);

                    showAlertDialogScores();

                    //Toast.makeText(ClassicPlayActivity.this,"Bitti",Toast.LENGTH_SHORT).show();
                }

                if (textViewPlaySecondsCounter.getText().toString().equals("00:00") && textViewPlayClassicTeamA.getText().toString().equals(teamsList.get(0).getTeamAname())){
                    /*Score score = new Score(correctCounter,0);
                    scoreList.clear();
                    scoreList.add(score);*/

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
        if (textViewPlayClassicTeamA.getText().toString().equals(teamsList.get(0).getTeamBname())){
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassicPlayActivity.this);
            builder.setCancelable(false);
            builder.setTitle("SKORLAR");
            builder.setMessage(teamsList.get(0).getTeamAname()+": "+scoreAList.get(0).getScoreA()+"\n"+teamsList.get(0).getTeamBname()+": "+scoreBList.get(0).getScoreB());
            builder.setPositiveButton("YENİ OYUN OYNA", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
                    boolean buyprocess2 = sharedPreferences.getBoolean("buynoads",false);

                    if (!buyprocess2){
                        if (rewardedAd.isLoaded()){
                            rewardedAd.show(ClassicPlayActivity.this,rewardedAdCallback);
                        }
                    }



                    passCounterA = passBothOfTeams;
                    passCounterB = passBothOfTeams;
                    textViewPlayClassicTeamA.setText(teamsList.get(0).getTeamAname());
                    scoreAList.clear();
                    scoreBList.clear();




                    correctCounter = 0;
                    textViewPlayScore.setText("SKOR: "+correctCounter);

                    //textViewPlayClassicTeamA.setText(teamsList.get(0).getTeamBname());
                    resetTimer();
                    startTimer();

                }
            });

            builder.create().show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassicPlayActivity.this);
            builder.setCancelable(false);
            builder.setTitle("SKORLAR");
            builder.setMessage(teamsList.get(0).getTeamAname()+": "+scoreAList.get(0).getScoreA()+"\n"+teamsList.get(0).getTeamBname()+": "+scoreBList.get(0).getScoreB());
            builder.setPositiveButton("DEVAM", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
                    boolean buyprocess = sharedPreferences.getBoolean("buynoads",false);

                    if (!buyprocess){
                        if (rewardedAd.isLoaded()){
                            rewardedAd.show(ClassicPlayActivity.this,rewardedAdCallback);
                        }
                    }

                    //scoreList.clear();
                    correctCounter = 0;
                    textViewPlayScore.setText("SKOR: "+correctCounter);
                    textViewPlayClassicTeamA.setText(teamsList.get(0).getTeamBname());
                    resetTimer();
                    startTimer();

                }
            });

            builder.create().show();
        }


    }

    private void updateCountDownText() {
        int minutes = (int) (TIME_LEFT_IN_MILLIS / 1000) / 60;
        int seconds = (int) (TIME_LEFT_IN_MILLIS) / 1000 % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        textViewPlaySecondsCounter.setText(timeLeftFormatted);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void resetTimer(){
        TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        updateCountDownText();

    }


    private void addTabuuCard() {

        Tabuu tabuu001 = new Tabuu("KAZIK","AĞAÇ","ODUN","ÇİVİ","DEMİR","OTURMAK");
        Tabuu tabuu002 = new Tabuu("KÖTEK","DAYAK","SOPA","YEMEK","ÖFKE","BÜYÜK");
        Tabuu tabuu003 = new Tabuu("ANİME","KORE","JAPON","KARİKATÜR","MANGA","NARUTO");
        Tabuu tabuu004 = new Tabuu("DUDAK OKUMAK","SESSİZ","HABER","ENGELLİ","AĞIZ","DİL");
        Tabuu tabuu005 = new Tabuu("HEZARFEN","POLİMAT","BİLGİ","BİLİM","İSLAM","AHMET ÇELEBİ");
        Tabuu tabuu006 = new Tabuu("TOLSTOY","RUSYA","YAZAR","SOVYET","EDEBİYAT","ANNA KARENNINA");
        Tabuu tabuu007 = new Tabuu("MUASIR","ÇAĞDAŞ","GELİŞMİŞ","ATATÜRK","MEDENİYET","SEVİYE");
        Tabuu tabuu008 = new Tabuu("KUŞBURNU","ÇAY","TOZ","KIRMIZI","TURUNCU","TATLI");
        Tabuu tabuu009 = new Tabuu("ERCİYES","KAR","DAĞ","YÜKSEK","SOĞUK","KAYSERİ");
        Tabuu tabuu010 = new Tabuu("NARGİLE","TÜTÜN","SİGARA","ZEHİR","İÇMEK","KAFE");
        Tabuu tabuu011 = new Tabuu("BUGATTI VEYRON","LAMBORGHINI","YARIŞ","ARABA","HIZ","PAHALI");
        Tabuu tabuu012 = new Tabuu("TABURCU OLMAK","İSTİRAHAT","BİTMEK","HASTALIK","HASTAHANE","ECZANE");
        Tabuu tabuu013 = new Tabuu("İNTİKAM","DAVA","SONRADAN","ÖÇ","TEPKİ","KARŞILIK");
        Tabuu tabuu014 = new Tabuu("ÜLSER","KANSER","MİDE","HASTALIK","DOKTOR","AĞRI");
        Tabuu tabuu015 = new Tabuu("ÖRGÜ","İP","YÜN","TIĞ","BABANNE","ÖNLÜK");
        Tabuu tabuu016 = new Tabuu("HAYVAR","BALIK","PAHALI","YEMEK","ŞAMPANYA","YUMURTA");
        Tabuu tabuu017 = new Tabuu("HATIRLAMAK","AKLINA GELMEK","SONRADAN","GEÇMİŞ","ESKİ","VAR");
        Tabuu tabuu018 = new Tabuu("KORSE","SIRT","BOYUN","DÜZELTMEK","RAHATLIK","EĞRİ");
        Tabuu tabuu019 = new Tabuu("BOWLING","TOP","DEVİRMEK","OYUN","AĞIR","DELİK");
        Tabuu tabuu020 = new Tabuu("SÜSPANSİYON","YAY","ZIPLAMAK","ARABA","BİSİKLET","DAĞ");
        Tabuu tabuu021 = new Tabuu("AKÜMÜLATÖR","ELEKTRİK","DEPO","ŞARJ","KUTUP","ARABA");
        Tabuu tabuu022 = new Tabuu("KULAKLIK","TAKMAK","MÜZİK","DİNLEMEK","VIDEO","FILM");
        Tabuu tabuu023 = new Tabuu("KUMPİR","PATATES","MISIR","SICAK","SOBA","FIRIN");
        Tabuu tabuu024 = new Tabuu("İSKENDER","TAVUK","ET","SOS","YOĞURT","LOKANTA");
        Tabuu tabuu025 = new Tabuu("ÇİĞKÖFTE","TÜRK","ACI SOS","NAR EKŞİSİ","AYRAN","DÜRÜM");
        Tabuu tabuu026 = new Tabuu("JAVA","KOTLIN","DART","FLUTTER","PROGRAMLAMA","MOBİL");
        Tabuu tabuu027 = new Tabuu("ECİŞ BÜCÜŞ","DÜZGÜN","EĞRİ","ÇARPIK","BOZUK","ÇİRKİN");
        Tabuu tabuu028 = new Tabuu("TIPKI","BENZEMEK","AYNI","KİŞİ","YÜZ","İFADE");
        Tabuu tabuu029 = new Tabuu("KIPIRTI","KALP","DUYGU","DÜŞÜNCE","HAREKET","OLAY");
        Tabuu tabuu030 = new Tabuu("CADILAR BAYRAMI","KORKUNÇ","NOEL","HRISTIYAN","AVRUPA","KOSTÜM");
        Tabuu tabuu031 = new Tabuu("YELLENMEK","İSHAL","TUVALET","ETMEK","İHTİYAÇ","HASTA");
        Tabuu tabuu032 = new Tabuu("EMEKÇİ","ÇALIŞMAK","SERMAYE","PARA","VESAİ","YAŞLI");
        Tabuu tabuu033 = new Tabuu("CİĞER","ORGAN","HAYVAN","KURBAN","İÇ","YEMEK");
        Tabuu tabuu034 = new Tabuu("KEFEN","MEZAR","MORG","HASTAHANE","ÖLÜM","CENAZE");
        Tabuu tabuu035 = new Tabuu("DEDE KORKUT","ERGENEKON DESTANI","EDEBİYAT","EFSANE","MAZMUN","ESKİ");
        Tabuu tabuu036 = new Tabuu("HZ. MUHAMMED (S.A.V)","ISLAM","PEYGAMBER","EMİN","ALLAH (C.C)","AHİRET");
        Tabuu tabuu037 = new Tabuu("GİRDAP","DENİZ","BOŞLUK","MARIANA","ÇUKUR","OLUŞMAK");
        Tabuu tabuu038 = new Tabuu("HAYAT","YAŞAM","DÖNGÜ","ZAMAN","DUYGU","GEÇİRMEK");
        Tabuu tabuu039 = new Tabuu("ŞAHAN GÖKBAHAR","RECEP İVEDİK","GÜLMEK","EZGİ MOLA","KAYHAN","OSMAN PAZARLAMA");
        Tabuu tabuu040 = new Tabuu("SALLANMAK","SALINCAK","PARK","ÇOCUK","RÜZGAR","HAVA");
        Tabuu tabuu041 = new Tabuu("TAKIM","MAÇ","OYUN","FUTBOL","VOLEYBOL","HENTBOL");
        Tabuu tabuu042 = new Tabuu("BORNOZ","TAKIM","HAVLU","YIKANMAK","BANYO","GİYMEK");
        Tabuu tabuu043 = new Tabuu("AĞLAMAK","BEBEK","İNSAN","HAYVAN","ACI","NEŞE");
        Tabuu tabuu044 = new Tabuu("EHEMMİYET","ÖNEM","TECRÜBE","ÖNCELİK","ZORLUK","ÜST");
        Tabuu tabuu045 = new Tabuu("SIRADAN","ORTALAMA","NORMAL","VARSAYILAN","MEVCUT","İNSAN");
        Tabuu tabuu046 = new Tabuu("VERİTABANI","SQL","SQLITE","FIREBASE","OFFLINE","DEPOLAMAK");
        Tabuu tabuu047 = new Tabuu("TOPLANTI","KONFERANS","ÜYE","ŞİRKET","GRUP","TOPLULUK");
        Tabuu tabuu048 = new Tabuu("CEMRE","HAVA","SU","TOPRAK","BAHAR","DÜŞMEK");
        Tabuu tabuu049 = new Tabuu("KAZAK","KIŞ","ÖRMEK","YÜN","KALIN","GİYSİ");
        Tabuu tabuu050 = new Tabuu("SÖKMEK","BOZMAK","OYNAMAK","ALET","IRDAVAT","TORNAVİDA");
        Tabuu tabuu051 = new Tabuu("KURGU","MONTAJ","OYNAMA","FILM","ÜNLÜ","BİLİM");
        Tabuu tabuu052 = new Tabuu("İKİLEM","SEÇENEK","BİR","KARAR","ARADA KALMAK","EMİN OLMAK");
        Tabuu tabuu053 = new Tabuu("KATEGORİ","AYIRMAK","TOPLU","GRUP","FILM","DİZİ");
        Tabuu tabuu054 = new Tabuu("NEŞET ERTAŞ","BOZKIR","ŞARKI","NEVŞEHİR","SAZ","SÖYLEMEK");
        Tabuu tabuu055 = new Tabuu("TUŞ","DAKTİLO","KLAVYE","TELEFON","BİLGİSAYAR","DÜĞME");
        Tabuu tabuu056 = new Tabuu("GICIK OLMAK","HOŞLANMAK","ÖFKE","SEVMEMEK","SİNİR BOZUCU","KÖTÜ");
        Tabuu tabuu057 = new Tabuu("KONSEY","GRUP","MİLLET VEKİLİ","GÜVENLİK","TOPLUM","MECLİS");
        Tabuu tabuu058 = new Tabuu("ASKER","BORDO BERELİ","GÜVENLİK","SEVER","ÜLKE","DÜŞMAN");
        Tabuu tabuu059 = new Tabuu("MICHAEL JORDAN","HELİKOPTER","KAZA","KIZ","ÖLMEK","BASKETBOL");
        Tabuu tabuu060 = new Tabuu("ARA BULUCU OLMAK","DÜZELTMEK","SEVMEK","ARKADAŞ","DOST","ARASINDA");
        Tabuu tabuu061 = new Tabuu("U DÖNÜŞÜ","KAVŞAK","DAR KAVİS","GENİŞ KAVİS","GERİ","YAPMAK");
        Tabuu tabuu062 = new Tabuu("MUSTAFA KEMAL ATATÜRK","CUMHURİYET","TÜRKİYE","ASKER","AKDENİZ","OSMANLI");
        Tabuu tabuu063 = new Tabuu("BAHÇELİ","PARTİ","MHP","PÜSKEVİT","DAVA","KURMAK");
        Tabuu tabuu064 = new Tabuu("TİKSİNMEK","İĞRENMEK","YEMEK","SEVMEMEK","BULANMAK","MIDE");
        Tabuu tabuu065 = new Tabuu("BAKIMSIZ","KENDİ","AYNA","YÜZ","ÖZENSİZ","İLGİ");

        //Yorum yapan kullanıcı için aşağıdaki açılacak
        Tabuu tabuu066 = new Tabuu("AYŞEKADIN","FASULYE","SEBZE","YEŞİL","İSİM","KADIN");
        Tabuu tabuu067 = new Tabuu("HOPARLÖR","RADYO","MÜZİK","SES","BİLGİSAYAR","TV");
        Tabuu tabuu068 = new Tabuu("ABSÜRD","GEREKSİZ","SAÇMA","ANLAMSIZ","UYGUNSUZ","KOMEDİ");
        Tabuu tabuu069 = new Tabuu("AĞIT","AĞLAMAK","CENAZE","DOĞU","BAĞIRMAK","TÜRKÜ");
        Tabuu tabuu070 = new Tabuu("GÜLLE","TOP","SPOR","SİYAH","OLİMPİYAT","OYUN");
        Tabuu tabuu071 = new Tabuu("EFES","İZMİR","ANTİK","KENT","TARİHİ","ESER");
        Tabuu tabuu072 = new Tabuu("BESTE","ŞARKI","MÜZİK","ÇALMAK","EZGİ","TÜRKÜ");
        Tabuu tabuu073 = new Tabuu("DEKORASYON","EV","DİZAYN","TASARIM","MİMAR","YENİLİK");
        Tabuu tabuu074 = new Tabuu("GALATASARAY","FERNANDO MUSLERA","FUTBOL","MAÇ","BASKETBOL","SPOR");
        Tabuu tabuu075 = new Tabuu("NBA","KOBE BRYANT","HİDAYET TÜRKOĞLU","CEDİ OSMAN","BASKETBOL","FUTBOL");
        Tabuu tabuu076 = new Tabuu("ADAPTÖR","UYUM","İLİŞKİ","BATARYA","TAKMAK","KABLO");
        Tabuu tabuu077 = new Tabuu("HİPODROM","KOŞU","VELİ EFENDİ","AT","YARIŞ","JOKEY");
        Tabuu tabuu078 = new Tabuu("HAVUZ","VİLLA","YÜZMEK","DENİZ","OTEL","SU");
        Tabuu tabuu079 = new Tabuu("PSİKOANALİZ","TELKİN","METOT","İKNA","İZZET GÜLLÜ","SEANS");
        Tabuu tabuu080 = new Tabuu("ORYANTAL","TANYELİ","MÜZİK","ORKESTRA","GRUP","MELODİ");
        Tabuu tabuu081 = new Tabuu("ŞARKICI","MÜZİK","SANAT","MURAT BOZ","HADİSE","MFÖ");
        Tabuu tabuu082 = new Tabuu("ÇÖPÇATAN","EVLENMEK","ÇİFT","ARACI OLMAK","BİR ARAYA GELMEK","MESLEK");
        Tabuu tabuu083 = new Tabuu("DRAKULA","FRANKEŞTAYN","VAMPİR","KURT ADAM","YARASA","KAN");
        Tabuu tabuu084 = new Tabuu("SANDAL","TEKNE","VAPUR","GONDOL","DENİZ","SEYEHAT");
        Tabuu tabuu085 = new Tabuu("ALİ KUŞÇU","MATEMATİK","BİLGİN","ALİM","OSMANLI","EDEBİYAT");
        Tabuu tabuu086 = new Tabuu("KOZA","KANAT","BÖCEK","KELEBEK","TIRTIL","ARI");
        Tabuu tabuu087 = new Tabuu("HİCRİ TAKVİM","MİLADİ","PEYGAMBER","AY","ZAMAN","HİCRET");
        Tabuu tabuu088 = new Tabuu("REDKIT","ÇİZGİ FİLM","TELEVİZYON","ESKİ","İZLEMEK","SEYRETMEK");
        Tabuu tabuu089 = new Tabuu("KAZANDİBİ","TATLI","MARKET","FIRIN","TRİLEÇE","SÜT");
        Tabuu tabuu090 = new Tabuu("BİAT ETMEK","KABUL ETMEK","UYMAK","KABULLENMEK","DİNİ","SÖZ");
        Tabuu tabuu091 = new Tabuu("ENGİN ALTAN DÜZYATAN","DİRİLİŞ ERTUĞRUL","SARIŞIN","SAÇ","DİZİ","YAKIŞIKLI");
        Tabuu tabuu092 = new Tabuu("ÇAĞATAY ULUSOY","İÇERDE","ARAS BULUT İYNEMLİ","HAKAN MUHAFIZ","DELİBAL","SERENAY SARIKAYA");
        Tabuu tabuu093 = new Tabuu("CEM YILMAZ","KOMEDYEN","KÜPE","BOĞAZİÇİ","İSTANBUL","TEK KİŞİ");
        Tabuu tabuu094 = new Tabuu("HIPOKRAT","HIPPOKRATES","YUNANİSTAN","TIP","İSTANKÖY","BİLİM");
        Tabuu tabuu095 = new Tabuu("TAŞINABİLİR DİSK","HARİCİ","SATA","VERİ","DEPOLAMAK","HARD DISK");
        Tabuu tabuu096 = new Tabuu("PARTİ","BİRLİKTELİK","ARKADAŞ","ÖZEL","GÜN","YABANCI");
        Tabuu tabuu097 = new Tabuu("TEBEŞİR","KARA TAHTA","BEYAZ","KİREÇ","ÇUBUK","OKUL");
        Tabuu tabuu098 = new Tabuu("OLİMPİYAT","OYUN","MADALYA","ESKİ","GEÇMİŞ","MAÇ");
        Tabuu tabuu099 = new Tabuu("KÜLTÜR","MİLLET","DEVLET","GELENEK","NESİL","MİSAFİR");


        Tabuu tabuu = new Tabuu("HAL EKİ","ÇEKİM EKİ","-İ HALİ","-DE HALİ","-DEN HALİ","-E HALİ");
        Tabuu tabuu1 = new Tabuu("YAPIM EKİ","EK","KELİME","KÖK","TÜREMİŞ","YAPI");
        Tabuu tabuu2 = new Tabuu("FİİLİMSİ","SIFAT FİİL","İSİM FİİL","ZARF FİİL","FİİL","EK");
        Tabuu tabuu3 = new Tabuu("EŞ SESLİ","YAZILIŞ","OKUNUŞ","AYNI","SÖZCÜK","SESTEŞ");
        Tabuu tabuu4 = new Tabuu("ZIT ANLAM","KARŞIT","SÖZCÜK","ÖRNEK","BİRBİRİNE","MANA");
        Tabuu tabuu5 = new Tabuu("FİİL","İŞ","OLUŞ","HAREKET","EYLEM","SÖZCÜK");
        Tabuu tabuu6 = new Tabuu("UYAK","ŞİİR","DİZE","BENZERLİK","KAFİYE","SES");
        Tabuu tabuu7 = new Tabuu("SÖZLÜK","ANLAM","KELİME","SÖZCÜK","AÇIKLAMA","LÜGAT");
        Tabuu tabuu8 = new Tabuu("KUŞBAKIŞI","HARİTA","TEPE","YUKARI","GÖRMEK","KROKİ");
        Tabuu tabuu9 = new Tabuu("OKKA","AĞIRLIK","ÖLÇÜ","BİRİM","KİLO","TARTI");
        Tabuu tabuu10 = new Tabuu("KÖK","SÖZCÜK","EK","YAPIM","ÇEKİM","KELİME");
        Tabuu tabuu11 = new Tabuu("ŞİİR","ŞAİR","MISRA","DİZE","KITA","DÖRTLÜK");
        Tabuu tabuu12 = new Tabuu("FABL","HAYVAN","LA FONTAINE","MASAL","İNSAN","HİKAYE");
        Tabuu tabuu13 = new Tabuu("ÖYKÜ","HİKAYE","KAHRAMAN","YAZAR","YER","KİTAP");
        Tabuu tabuu14 = new Tabuu("HAL EKİ","ÇEKİM EKİ","-İ HALİ","-DE HALİ","-DEN HALİ","-E HALİ");
        Tabuu tabuu15 = new Tabuu("YAPIM EKİ","EK","KELİME","KÖK","TÜREMİŞ","YAPI");
        Tabuu tabuu16 = new Tabuu("FİİLİMSİ","SIFAT FİİL","İSİM FİİL","ZARF FİİL","FİİL","EK");
        Tabuu tabuu17 = new Tabuu("EŞ SESLİ","YAZILIŞ","OKUNUŞ","AYNI","SÖZCÜK","SESTEŞ");
        Tabuu tabuu18 = new Tabuu("ZIT ANLAM","KARŞIT","SÖZCÜK","ÖRNEK","BİRBİRİNE","MANA");
        Tabuu tabuu19 = new Tabuu("MECAZ","KELİME","SÖZCÜK","GERÇEK ANLAM","YAN ANLAM","TERİM ANLAM");
        Tabuu tabuu20 = new Tabuu("ÖZNE","CÜMLE","İŞ","KİŞİ","VARLIK","ÖĞE");
        Tabuu tabuu21 = new Tabuu("BENZEŞME","SERT","ÜNSÜZ","FISTIKÇI ŞAHAP","GeCiD","KURAL");
        Tabuu tabuu22 = new Tabuu("MASAL","OLAĞANÜSTÜ","KAHRAMAN","YAZAR","KELOĞLAN","KİTAP");
        Tabuu tabuu23 = new Tabuu("TÜREMİŞ","SÖZCÜK","KELİME","YAPIM EKİ","ÇEKİM EKİ","YAPI");
        Tabuu tabuu24 = new Tabuu("ÜNSÜZ YUMUŞAMASI","KETÇAP","HARF","SES","ÜNLÜ","SERTLEŞME");
        Tabuu tabuu25 = new Tabuu("FAY","HAT","DEPREM","KIRILMAK","SARSILMAK","İSTANBUL");
        Tabuu tabuu26 = new Tabuu("KONUŞTURMA","İNTAK","HAYVAN","CANSIZ","VARLIK","SANAT");
        Tabuu tabuu27 = new Tabuu("ÖNERİ","TEKLİF","ÇÖZÜM","YOL","CÜMLE","İYİ OLUR");
        Tabuu tabuu28 = new Tabuu("KÜÇÜMSEME","NİTELİK","DEĞERSİZ","YARGI","CÜMLE","AZIMSAMA");
        Tabuu tabuu29 = new Tabuu("İKİLEM","KARARSIZ","CÜMLE","DURUM","ACABA","TERCİH");
        Tabuu tabuu30 = new Tabuu("VARSAYIM","OLMAMIŞ","TUT Kİ","FARZ ET","DİYELİM","CÜMLE");



        Tabuu tabuu31 = new Tabuu("AKSİ","SİNİRLİ","TERS","ZIT","ASABİ","HUYSUZ");
        Tabuu tabuu32 = new Tabuu("VEZNE","BANKA","PARA","ÖDEME","ALMAK","MUHASEBE");
        Tabuu tabuu33 = new Tabuu("OLUK","AKMAK","SU","YAĞMUR","SAÇAK","BORU");
        Tabuu tabuu34 = new Tabuu("CEPHE","SAVAŞ","ORDU","ASKER","SALDIRMAK","BARIŞ");
        Tabuu tabuu35 = new Tabuu("ÇEVİK","KUVVET","POLİS","ASKER","GÜÇ","HIZLI");
        Tabuu tabuu36 = new Tabuu("YAMAÇ","UÇURUM","YÜKSEK","DAĞ","DİK","PARAŞÜT");
        Tabuu tabuu37 = new Tabuu("PİL","KALEM","İNCE","KUMANDA","EL FENERİ","FEN");
        Tabuu tabuu38 = new Tabuu("ZİRAAT","TARIM","ÇİFTÇİ","TOPRAK","HAYVAN","BANKA");
        Tabuu tabuu39 = new Tabuu("PROSPEKTÜS","İLAÇ","ECZANE","OKUMAK","İÇİNDEKİLER","KULLANMAK");
        Tabuu tabuu40 = new Tabuu("ESKİMO","KUTUP","BUZUL","KAR","SOĞUK","BALIK");
        Tabuu tabuu41 = new Tabuu("HAMAK","YATMAK","AĞAÇ","SALLANMAK","KURMAK","PİKNİK");
        Tabuu tabuu42 = new Tabuu("GADDAR","ACIMASIZ","SERT","KATI","MERHAMET","İNSAF");
        Tabuu tabuu43 = new Tabuu("ÇADIR","KAMP","UYKU TULUMU","KURMAK","DOĞA","TATİL");
        Tabuu tabuu44 = new Tabuu("NADAS","TARLA","EKMEK","BIRAKMAK","DİNLENDİRMEK","ÇİFTÇİ");
        Tabuu tabuu45 = new Tabuu("HASIR","SEPET","ŞAPKA","PLAJ","DENİZ","SERMEK");
        Tabuu tabuu46 = new Tabuu("BUKELAMUN","HAYVAN","RENK","KERTENKELE","DEĞİŞMEK","SÜRÜNGEN");
        Tabuu tabuu47 = new Tabuu("KONVOY","ARABA","GİTMEK","DİZİLMEK","ARKA","DÜĞÜN");
        Tabuu tabuu48 = new Tabuu("TAKUNYA","TAHTA","TERLİK","AYAKKABI","GİYMEK","HAMAM");
        Tabuu tabuu49 = new Tabuu("SERVET","KAZANMAK","ZENGİNLİK","MAL","MÜLK","PARA");
        Tabuu tabuu50 = new Tabuu("TEDARİK","BULMAK","SAĞLAMAK","MALZEME","ETMEK","HAZIRLIK");
        Tabuu tabuu51 = new Tabuu("TABURE","OTURMAK","SANDALYE","KOLTUK","YEMEKHANE","SIRT");



        Tabuu tabuu52 = new Tabuu("EREZYON","Toprak","Kayma","Ağaç","Heyelan","TEMA");
        Tabuu tabuu53 = new Tabuu("ÇAMAŞIR","Kirli","Yıkamak","Makine","Deterjan","Giymek");
        Tabuu tabuu54 = new Tabuu("NUMUNE","Örnek","Vermek","Tahlil","Yemek","KÜÇÜK");
        Tabuu tabuu55 = new Tabuu("VİRAN","Eski","Yıkık","Harap","Ev","Köhne");
        Tabuu tabuu56 = new Tabuu("KASVET","Sıkıntı","Gam","Karanlık","Aydınlık","Neşeli");
        Tabuu tabuu57 = new Tabuu("MİÇO","Tayfa","Gemi","Kaptan","Deniz","Yardımcı");
        Tabuu tabuu58 = new Tabuu("KABİN","Giyinmek","DUŞ","Denemek","Mağaza","Kıyafet");
        Tabuu tabuu59 = new Tabuu("PORSİYON","Yemek","Tabak","Lokanta","Yarım","Restoran");
        Tabuu tabuu60 = new Tabuu("DOZ","Miktar","İLAÇ","Doktor","REÇETE","AŞIRI");
        Tabuu tabuu61 = new Tabuu("DİYAR","Memleket","Vatan","Dolasmak","Gezmek","Âsık Veysel");
        Tabuu tabuu62 = new Tabuu("BOŞBOĞAZ","Konuşmak","Geveze","Sır","Söylemek","Anlatmak");
        Tabuu tabuu63 = new Tabuu("MONOTON","Aynı","Sıkıcı","Sıradan","Benzer","Rutin");
        Tabuu tabuu64 = new Tabuu("YADIRGAMAK","Garip","Tuhaf","Kabullenmek","ŞAŞIRMAK","DAVRANIŞ");
        Tabuu tabuu65 = new Tabuu("ZEYBEK","Atatürk","Oyun","Ege","Efe","Sarı");
        Tabuu tabuu66 = new Tabuu("FESHETMEK","ANTLAŞMA","SÖZLEŞME","İptal","GEÇERSİZ","Bozmak");
        Tabuu tabuu67 = new Tabuu("PARANOYA","Şüphe","Akıl","Ruh","Hastalık","Deli");
        Tabuu tabuu68 = new Tabuu("GÜZERGÂH","Yol","Araba","Rota","Servis","Takip Etmek");
        Tabuu tabuu69 = new Tabuu("PERFORMANS","Degerlendirme","Basarı","Ders","Ödev","Yüksek");
        Tabuu tabuu70 = new Tabuu("UCUZLUK","İndirim","Pahalı","Yüzde","Fiyat","Vitrin");
        Tabuu tabuu71 = new Tabuu("ABLUKA","Etraf","KUŞATMA","SAVAŞ","Çevirmek","DÜŞMAN");
        Tabuu tabuu72 = new Tabuu("VERESİYE","PEŞİN","BORÇ","Satın Almak","Ödemek","Defter");
        Tabuu tabuu73 = new Tabuu("PATİKA","Keçi Yolu","Orman","Kestirme","Yürümek","DAĞ");
        Tabuu tabuu74 = new Tabuu("BUĞU","Buhar","Cam","Su","Sıcak","Araba");
        Tabuu tabuu75 = new Tabuu("ÇAVDAR","Arpa","Tahıl","BUĞDAY","Kepek","Ekmek");
        Tabuu tabuu76 = new Tabuu("NADİR","Zor","Az","Bulmak","Sık","Çok");
        Tabuu tabuu77 = new Tabuu("MIZIKÇI","Çocuk","Küsmek","Darılmak","Bozmak","Oyun");
        Tabuu tabuu78 = new Tabuu("ESİR","Mahkûm","SAVAŞ","DÜŞMEK","Tutsak","Kamp");

        Tabuu tabuu79 = new Tabuu("YADİGAR","Aile","Hatıra","DEĞERLİ","Miras","Bırakmak");
        Tabuu tabuu80 = new Tabuu("SISKA","ZAYIF","İNCE","ÇELİMSİZ","ŞİŞMAN","HASTA");
        Tabuu tabuu81 = new Tabuu("SUİSTİMAL","İYİ","NİYET","KULLANMAK","FAYDALANMAK","KÖTÜ");
        Tabuu tabuu82 = new Tabuu("MACUN","CAM","PENCERE","TUTMAK","KENAR","MESİR");
        Tabuu tabuu83 = new Tabuu("TECRÜBE","İŞ","KAZANMAK","ÇALIŞMAK","DENEYİM","YIL");
        Tabuu tabuu84 = new Tabuu("ARIZA","BOZUK","TAMİR","ÇALIŞMAK","TELEFON","ELEKTRİK");
        Tabuu tabuu85 = new Tabuu("STAJ","ÖĞRENCİ","ÇALIŞMAK","ÜNİVERSİTE","TECRÜBE","İŞ");
        Tabuu tabuu86 = new Tabuu("SIRIK","UZUN","ATLAMAK","BOY","ATLETİZM","FASULYE");
        Tabuu tabuu87 = new Tabuu("KAOS","KARIŞIKLIK","ORTAM","KARGAŞA","DÜZEN","YARATMAK");
        Tabuu tabuu88 = new Tabuu("FULAR","EŞARP","BAĞLAMAK","BAŞ","BOYUN","KADIN");
        Tabuu tabuu89 = new Tabuu("PLAKET","ÖDÜL","TÖREN","BAŞARI","VERMEK","TEŞEKKÜR");
        Tabuu tabuu90 = new Tabuu("FİRAR","KAÇAK","HAPİS","ASKER","MAHKUM","ETMEK");
        Tabuu tabuu91 = new Tabuu("HALAY","DÜĞÜN","ÇEKMEK","OYNAMAK","MENDİL","KOL");
        Tabuu tabuu92 = new Tabuu("HİLE","ALDATMAK","KANDIRMAK","OYUN","YAPMAK","KUMAR");
        Tabuu tabuu93 = new Tabuu("PASAKLI","TEMİZ","TİTİZ","KİRLİ","DÜZENLİ","KARIŞIK");
        Tabuu tabuu94 = new Tabuu("UYANIK","AKILLI","ZEKİ","AÇIKGÖZ","KURNAZ","SAF");
        Tabuu tabuu95 = new Tabuu("İHALE","BELEDİYE","GİRMEK","AÇMAK","YOLSUZLUK","KAZANMAK");
        Tabuu tabuu96 = new Tabuu("İŞTAH","ACIKMAK","KESİLMEK","AÇMAK","LEZZET","YEMEK");
        Tabuu tabuu97 = new Tabuu("AJANDA","DEFTER","İŞ","YAZMAK","GÜN","TOPLANTI");
        Tabuu tabuu98 = new Tabuu("MÜSVEDDE","KARALAMA","NOT ALMAK","YAZMAK","KAĞIT","TEMİZ");
        Tabuu tabuu99 = new Tabuu("FLAMA","BAYRAK","ÜÇGEN","OKUL","İZCİ","TÖREN");
        Tabuu tabuu100 = new Tabuu("SİFTAH","İLK","GÜN","SATMAK","MAL","ALIŞVERİŞ");


        Tabuu tabuu101 = new Tabuu("TALİMAT","EMİR","VERMEK","FATURA","BANKA","OTOMATİK ÖDEME");
        Tabuu tabuu102 = new Tabuu("ÇUVAL","TORBA","DOLDURMAK","YEM","UN","KOYMAK");
        Tabuu tabuu103 = new Tabuu("GÖÇMEN","MÜLTECİ","VİZE","SOĞUK","KUŞ","SICAK");
        Tabuu tabuu104 = new Tabuu("YELPAZE","SICAK","RÜZGAR","YAZ","KADIN","SALLAMAK");
        Tabuu tabuu105 = new Tabuu("YABANİ","VAHŞİ","İLKEL","HAYAT","ORMAN","HAYVAN");
        Tabuu tabuu106 = new Tabuu("BAMBU","SAZLIK","MOBİLYA","AĞAÇ","MASA","SANDALYE");
        Tabuu tabuu107 = new Tabuu("AKROSTİŞ","ŞİİR","MISRA","İSİM","İLK","KITA");
        Tabuu tabuu108 = new Tabuu("YAYIK","AYRAN","SU","YOĞURT","ÇALKALAMAK","SÜT");
        Tabuu tabuu109 = new Tabuu("KÖHNE","ESKİ","TARİHİ","YIKILMAK","BİNA","HARABE");
        Tabuu tabuu110 = new Tabuu("KÜREMEK","KAR","EV","YOL","BUZ","KIŞ");
        Tabuu tabuu111 = new Tabuu("MESAİ","SAAT","İŞ","FAZLA","AKŞAM","KALMAK");
        Tabuu tabuu112 = new Tabuu("MİSKİN","TEMBEL","UYUŞUK","YAVAŞ","AĞIR","KEDİ");
        Tabuu tabuu113 = new Tabuu("KIVILCIM","ATEŞ","KİBRİT","ÇAKMAK","TAŞ","SÜRTMEK");
        Tabuu tabuu114 = new Tabuu("PRATİK","KOLAY","ZEKA","ÇABUK","HIZLI","ÇÖZÜM");
        Tabuu tabuu115 = new Tabuu("FİDAN","AĞAÇ","BÜYÜMEK","KÜÇÜK","ORMAN","DİKMEK");
        Tabuu tabuu116 = new Tabuu("MANİ","KISA","ŞİİR","EDEBİYAT","SAKIZ","ENGEL");
        Tabuu tabuu117 = new Tabuu("ÇIRA","ATEŞ","YAKMAK","ODUN","TAHTA","ÇAY");
        Tabuu tabuu118 = new Tabuu("HEYBETLİ","YÜCE","DAĞ","BÜYÜK","YÜKSEK","İRİ");
        Tabuu tabuu119 = new Tabuu("BONKÖR","ELİ AÇIK","CÖMERT","PARA","GÖNLÜ ZENGİN","HARCAMAK");
        Tabuu tabuu120 = new Tabuu("ULEMA","BİLGİN","OSMANLI","DİN","ALİM","HOCA");
        Tabuu tabuu121 = new Tabuu("ÖZÇEKİM","SELFIE","TELEFON","KENDİ","FOTOĞRAF","POZ");
        Tabuu tabuu122 = new Tabuu("KİŞİLEŞTİRME","İNSAN","FABL","HAYVAN","VARLIK","KONUŞTURMA");
        Tabuu tabuu123 = new Tabuu("HOŞGÖRÜ","MEVLANA","ANLAYIŞ","EMPATİ","NE OLURSAN","GEL");
        Tabuu tabuu124 = new Tabuu("EMPATİ","KENDİNİ","BAŞKASI","YERİNE","KOYMA","DÜŞÜNME");
        Tabuu tabuu125 = new Tabuu("ANAHTAR","KİLİT","METAL","KASA","KAPI","ÇİLİNGİR");
        Tabuu tabuu126 = new Tabuu("KEDİ","PATİ","FARE","TÜY","KUYRUK","HAYVAN");
        Tabuu tabuu127 = new Tabuu("KAPAN","AV","KURT","HAYVAN","TUZAK","FARE");
        Tabuu tabuu128 = new Tabuu("DERGİ","GAZETE","MECMUA","MAKALE","YAZI","KAPAK");
        Tabuu tabuu129 = new Tabuu("DOST","GÜVEN","SAMİMİ","DÜRÜST","ARKADAŞ","AHLAKLI");
        Tabuu tabuu130 = new Tabuu("BAĞLAMA","TÜRKÜ","MÜZİK","SAZ","TEL","AKORT");
        Tabuu tabuu131 = new Tabuu("TİYATRO","OYUNCU","SAHNE","PERDE","OYUN","SUFLÖR");
        Tabuu tabuu132 = new Tabuu("MALA","İNŞAAT","DUVAR","USTA","SIVA","ÇİMENTO");
        Tabuu tabuu133 = new Tabuu("GÖKKUŞAĞI","RENKLİ","HAVA","GÜNEŞ","YAĞMUR","EBEM");
        Tabuu tabuu134 = new Tabuu("KELEBEK","RENKLİ","UÇMAK","TIRTIL","KOZA","HAYVAN");
        Tabuu tabuu135 = new Tabuu("FERMUAR","PANTOLON","MONT","GİYSİ","KIYAFET","KOT");
        Tabuu tabuu136 = new Tabuu("BALMUMU","MUM","ERİMEK","HEYKEL","MÜZE","BAL");
        Tabuu tabuu137 = new Tabuu("PARAŞÜT","UÇAK","ATLAMAK","UÇMAK","BALON","HAVA");
        Tabuu tabuu138 = new Tabuu("MANGALA","OYUN","OSMANLI","KUYU","TAŞ","HAZİNE");
        Tabuu tabuu139 = new Tabuu("SATRANÇ","ŞAH-MAT","KALE","VEZİR","PİYON","FİL");
        Tabuu tabuu140 = new Tabuu("ADEM ELMASI","ERKEK","GIRTLAK","ÇIKINTI","BOĞAZ","HAVVA");
        Tabuu tabuu141 = new Tabuu("ANTİKA","MÜZAYEDE","ZENGİN","ESKİ","TABLO","TARİHİ");
        Tabuu tabuu142 = new Tabuu("KUMBARA","PARA","BİRİKTİRMEK","YATIRIM","BANKA","SAKLAMAK");
        Tabuu tabuu143 = new Tabuu("PİŞMANLIK","HATA","ÜZÜLMEK","YANLIŞ","KEŞKE","SON");
        Tabuu tabuu144 = new Tabuu("KABZIMAL","MEYVE","SEBZE","HAL","SATMAK","ARACI");
        Tabuu tabuu145 = new Tabuu("ARMAĞAN","HEDİYE","VERMEK","ALMAK","DOĞUM GÜNÜ","SEVİNDİRMEK");
        Tabuu tabuu146 = new Tabuu("DUY","TAVAN","LAMBA","IŞIK","İŞİTMEK","ANAHTAR");
        Tabuu tabuu147 = new Tabuu("NİHALE","ALTLIK","TENCERE","ÇAYDANLIK","SICAK","TEZGAH");
        Tabuu tabuu148 = new Tabuu("KORNİŞ","TAVAN","PERDE","ASMAK","PENCERE","CAM");
        Tabuu tabuu149 = new Tabuu("TIRABZAN","MERDİVEN","KORKULUK","İNMEK","ÇIKMAK","BİNA");
        Tabuu tabuu150 = new Tabuu("ŞAMDAN","MUM","IŞIK","AYDINLIK","YAKMAK","SÜS");
        Tabuu tabuu151 = new Tabuu("BANDANA","MASTERCHEF","TAKMAK","GİYSİ","EMİR","ALIŞVERİŞ");


        Tabuu tabuu152 = new Tabuu("HALÜSİNASYON","HAYAL","GERÇEK","GÖRMEK","İLLÜZYON","SERAP");
        Tabuu tabuu153 = new Tabuu("CEMRE","DÜŞMEK","HAVA","TOPRAK","SU","BAHAR");
        Tabuu tabuu154 = new Tabuu("ÇAYLAK","ACEMİ","DENEYİM","TECRÜBE","USTA","YENİ");
        Tabuu tabuu155 = new Tabuu("HARABE","YIKIK","ESKİ","TARİHİ","EFES","GEZMEK");
        Tabuu tabuu156 = new Tabuu("BASKÜL","TARTI","KİLO","AĞIR","ÖLÇÜ","HAFİF");
        Tabuu tabuu157 = new Tabuu("KOORDİNAT","YER","ENLEM","BOYLAM","BELİRTMEK","VERMEK");
        Tabuu tabuu158 = new Tabuu("AVİZE","LAMBA","KRİSTAL","TAVAN","IŞIK","AYDINLIK");
        Tabuu tabuu159 = new Tabuu("KURNA","HAMAM","YIKANMAK","SU","TELLAK","GÖBEK TAŞI");
        Tabuu tabuu160 = new Tabuu("KÜSMEK","DARILMAK","KIZMAK","KONUŞMAK","TARTIŞMAK","KAVGA");
        Tabuu tabuu161 = new Tabuu("HECE","KELİME","HARF","SES","OKUMAK","YAZI");
        Tabuu tabuu162 = new Tabuu("AHESTE","YAVAŞ","AĞIR","HIZLI","DURGUN","TEMBEL");
        Tabuu tabuu163 = new Tabuu("KARŞILAŞTIRMA","KIYASLAMA","MUKAYESE","BENZERLİK","FARKLILIK","İKİ");
        Tabuu tabuu164 = new Tabuu("TERİM","KELİME","ANLAM","BİLİM","SANAT","KAVRAM");
        Tabuu tabuu165 = new Tabuu("AŞAMALILIK","GİDEREK","GİTTİKÇE","KADEME","CÜMLE","ANLAM");
        Tabuu tabuu166 = new Tabuu("KİP","DİLEK","FİİL","HABER","BİLDİRME","YÜKLEM");
        Tabuu tabuu167 = new Tabuu("EK FİİL","EYLEM","İSİM","BİRLEŞİK","ŞART","YÜKLEM");
        Tabuu tabuu168 = new Tabuu("ZARF","BELİRTEÇ","ZAMAN","YER - YÖN","DURUM","ZAMAN");
        Tabuu tabuu169 = new Tabuu("NOKTA","CÜMLE","SON","İŞARET","TARİH","SAAT");
        Tabuu tabuu170 = new Tabuu("SOMUT","DUYU","ALGILAMAK","SOYUT","İSİM","GÖRÜLEN");
        Tabuu tabuu171 = new Tabuu("SOYUT","DUYU","ALGILAMAK","GÖRÜLMEYEN","İSİM","SOMUT");
        Tabuu tabuu172 = new Tabuu("ZAMİR","İSİM","İŞARET","KİŞİ","ADIL","KELİME");
        Tabuu tabuu173 = new Tabuu("SIFAT","ÖN AD","NİTELEME","İSİM","ZAMİR","İŞARET");
        Tabuu tabuu174 = new Tabuu("OLASILIK","CÜMLE","ANLAM","İHTİMAL","BELKİ","OLABİLİR");
        Tabuu tabuu175 = new Tabuu("HAYIFLANMA","PİŞMANLIK","ÜZÜNTÜ","GEÇMİŞ","CÜMLE","ANLAM");
        Tabuu tabuu176 = new Tabuu("ÜÇ NOKTA","NOKTALAMA","SON","TAMAMLANMAMIŞ","İSTENMEYEN","CÜMLE");
        Tabuu tabuu177 = new Tabuu("ÖDEV","DERS","ÇALIŞMAK","OKUL","ÖĞRETMEN","EV");
        Tabuu tabuu178 = new Tabuu("ABARTMA","MÜBALAĞA","AŞIRI","FARKLI","GÖSTERMEK","OLDUĞUNDAN");
        Tabuu tabuu179 = new Tabuu("BENZETME","BENZEYEN","BENZETİLEN","GİBİ","GÜÇLÜ","GÜÇSÜZ");
        Tabuu tabuu180 = new Tabuu("KOŞUL","ŞART","NEDEN","CÜMLE","EĞER","SONUÇ");
        Tabuu tabuu181 = new Tabuu("ANLAM KAYMASI","FİİL","KİP","ZAMAN","ANLAM","EYLEM");
        Tabuu tabuu182 = new Tabuu("ANLATIM BİÇİMİ","BETİMLEME","ÖYKÜLEME","TARTIŞMACI","AÇIKLAYICI","PARAGRAF");
        Tabuu tabuu183 = new Tabuu("PARAGRAF","SINAV","SORU","ANA DÜŞÜNCE","YARDIMCI DÜŞÜNCE","METİN");
        Tabuu tabuu184 = new Tabuu("İNTERNET","ADSL","CHAT","MODEM","4.5G","MOBİL VERİ");
        Tabuu tabuu185 = new Tabuu("KİRLİ SAKAL","KIL","BIRAKMAK","YÜZ","ERKEK","TRAŞ");
        Tabuu tabuu186 = new Tabuu("ÇİT","TAHTA","BAHÇE","TARLA","ÇEVRE","SARMAK");
        Tabuu tabuu187 = new Tabuu("BILL GATES","WINDOWS","OFFICE","MELINDA GATES","BİLGİSAYAR","GIRISIMCI");
        Tabuu tabuu188 = new Tabuu("SELÇUK BAYRAKTAR","İNSANSIZ HAVA ARACI","YERLİ","MİLLİ","SAVUNMA","DIŞ GÜÇ");
        Tabuu tabuu189 = new Tabuu("ELON MUSK","AFRİKA","TESLA","NEURALINK","RECEP TAYYIP ERDOGAN","SOLAR CITY");
        Tabuu tabuu190 = new Tabuu("KLAVYE","TIK TIK","YAZMAK","ARAÇ","MOUSE","TELEFON");
        Tabuu tabuu191 = new Tabuu("TOGG","GIRISIM","OTOMOBİL","ARABA","YERLİ","MİLLİ");
        Tabuu tabuu192 = new Tabuu("ÇUKUR","KASİS","COĞRAFİ","DELİK","KUYU","FİZİK");
        Tabuu tabuu193 = new Tabuu("ZEKAT","MAL","KIRKTA BİR","ZENGİN","FAKİR","PARA");
        Tabuu tabuu194 = new Tabuu("HAC","KABE","MEKKE","TAVAF","İBADET","İHRAM");
        Tabuu tabuu195 = new Tabuu("PETROL","BENZİN","MAZOT","YAKIT","AKARYAKIT","OTOMOBİL");
        Tabuu tabuu196 = new Tabuu("KOLONYA","LİMON","ALKOL","CORONA","DEZENFEKTAN","ASİT");
        Tabuu tabuu197 = new Tabuu("BANABİ","PEMBE","MARKET","DARK STORE","PAPEL","SİPARİŞ");
        Tabuu tabuu198 = new Tabuu("TEKNOLOJİ","ÜRÜN","BİLGİSAYAR","ÜRETMEK","KOLAY","YENİ");
        Tabuu tabuu199 = new Tabuu("ENDÜSTRİ 4.0","SANAYİ DEVRİMİ","DÖRT","GELİŞMEK","YÖNTEM","ÜRETİM");
        Tabuu tabuu200 = new Tabuu("UZAY","NASA","BLUE ORIGIN","İSTİKBAL","SATURN","METEOR");
        Tabuu tabuu201 = new Tabuu("SU","İÇMEK","İNSAN","ATEŞ","BARAJ","BARDAK");
        Tabuu tabuu202 = new Tabuu("MASTERCHEF","ACUN ILICALI","JUNIOR","TAKIM","ŞEF","ŞAMPİYON");
        Tabuu tabuu203 = new Tabuu("MÜGE ANLI","KAYIP","TATLI SERT","PSİKOLOG","PSİKİYATRİST","KADIN");
        Tabuu tabuu204 = new Tabuu("HAKAN ŞÜKÜR","FUTBOLCU","SİYASET","GALATASARAY","FENERBAHÇE","SAPANCA");
        Tabuu tabuu205 = new Tabuu("KABUL ETMEK","ONAYLAMAK","TAMAM","ERMEK","İMKAN","YAPMAK");
        Tabuu tabuu206 = new Tabuu("POŞET","PLASTİK","PARALI MARKET","KUMAŞ","TORBA","KOYMAK");
        Tabuu tabuu207 = new Tabuu("AVAREL","HAYDUT","UZUN","REDKIT","DALTON","ÇİZİK");
        Tabuu tabuu208 = new Tabuu("POLEN","BAL","ARI","BÖCEK","UÇMAK","SARI");
        Tabuu tabuu209 = new Tabuu("KÜLYUTMAZ","HABABAM","SINIF","KOPYA","HİLE","KURAL");
        Tabuu tabuu210 = new Tabuu("VERESİYE VERMEK","SONRA","BAKKAL","ELDE ETMEK","KABUL","PARA");
        Tabuu tabuu211 = new Tabuu("VİCDAN AZABI","ÇEKMEK","PİŞMAN","ACI","DUYGU","KÖTÜ");
        Tabuu tabuu212 = new Tabuu("NOEL","KUTLAMAK","HARAM","YILBAŞI","EĞLENCE","KIRMIZI");
        Tabuu tabuu213 = new Tabuu("MALTA","ADA","SÜRGÜN","ŞAİR","NAMIK KEMAL","ŞİNASİ");
        Tabuu tabuu214 = new Tabuu("PASKALYA","HRİSTİYAN","YAHUDİ","BAYRAM","YUMURTA","SENE");
        Tabuu tabuu215 = new Tabuu("GINA GELMEK","USANMAK","YAPMAK","BIKMAK","SIKILMAK","BEZDİRMEK");
        Tabuu tabuu216 = new Tabuu("BANT","KAĞIT","YAPIŞMAK","KOLİ","JEL","PARA");
        Tabuu tabuu217 = new Tabuu("GÖZÜPEK","KAVGACI","ATILGAN","CESUR","KORKMAK","İLERİ");
        Tabuu tabuu218 = new Tabuu("KABARTMA TOZU","BEYAZ","FIRIN","KARBONAT","MARKET","KEK");
        Tabuu tabuu219 = new Tabuu("KULAK MİSAFİRİ OLMAK","DUYMAK","DEDİKODU","KAPI","ARKA","BAŞKASI");
        Tabuu tabuu220 = new Tabuu("TALİP","EVLİLİK","İZDİVAÇ","SEVGİ","ADAY","MİSAFİR");
        Tabuu tabuu221 = new Tabuu("KONFERANS","EDEBİYAT","TÜRKÇE","HABER","TELEVİZYON","SEMPOZYUM");
        Tabuu tabuu222 = new Tabuu("PALDIR KÜLDÜR","HABERSİZ","EV","MİSAFİR","DAVETSİZ","UÇMAK");
        Tabuu tabuu223 = new Tabuu("EZBERLEMEK","HAFIZA","BİLGİ","HAFIZ","SINAV","SÖZEL");
        Tabuu tabuu224 = new Tabuu("MIZIKÇI","OYUN","DÜZENBAZ","KUMARBAZ","BOZMAK","ÇOCUK");
        Tabuu tabuu225 = new Tabuu("HAREKET SİSTEMİ","ETMEK","YÜRÜMEK","KOŞMAK","BOŞALTIM","DOLAŞIM");
        Tabuu tabuu226 = new Tabuu("ÇİZMEYİ AŞMAK","HADDİNİ AŞMAK","SINIR","BİLMEK","ÇİZİK","SABIR");
        Tabuu tabuu227 = new Tabuu("HADDİ AŞMAK","ÇİZMEYİ AŞMAK","SABIRSIZ","ÖFKE","GERİLMEK","ARKADAŞ");
        Tabuu tabuu228 = new Tabuu("BAŞ HEKİM","DOKTOR","AMELİYAT","CERRAH","HASTANE","ÜST");
        Tabuu tabuu229 = new Tabuu("VURDUMDUYMAZ","BAYHAN","ŞARKI","GAMSIZ","DÜŞÜNMEK","HIZLI");
        Tabuu tabuu230 = new Tabuu("DEĞİRMEN","UN","PERVANE","TEPE","EKMEK","KURT");
        Tabuu tabuu231 = new Tabuu("SEMAVER","ÇAY","SICAK","PİKNİK","DEMLEMEK","TERMOS");
        Tabuu tabuu232 = new Tabuu("SAFKAN","AT","IRK","CİNS","KARŞI","AT");
        Tabuu tabuu233 = new Tabuu("ALTTAN ALMAK","POZİTİF","YUMUŞAK","KARŞI","OLUMLU","TARAF");
        Tabuu tabuu234 = new Tabuu("PARSEL","FAYANS","ARSA","BÖLÜK","SATMAK","PARÇA");
        Tabuu tabuu235 = new Tabuu("KIYASIYA","REKABET","MÜCADELE","ZOR","KIRAN KIRANA","ÇEKİŞMELİ");
        Tabuu tabuu236 = new Tabuu("KARAOKE","MİKROFON","ŞARKI","VIDEO","SOYLEMEK","SÖZ");
        Tabuu tabuu237 = new Tabuu("TİK TOK","GENÇ","KAPATMAK","AMERİKA","KISA VIDEO","BYTEDANCE");
        Tabuu tabuu238 = new Tabuu("KARA KUTU","SES","KAYIT","UÇAK","ENKAZ","PİLOT");
        Tabuu tabuu239 = new Tabuu("SECCADE","KILMAK","NAMAZ","EZAN","ÜZERİNDE","İKİNDİ");
        Tabuu tabuu240 = new Tabuu("KABİLE","AFRİKA","GRUP","SİYAHİ","YAŞAMAK","FAKİR");
        Tabuu tabuu241 = new Tabuu("EJDERHA","DUMAN","ATEŞ","VAHŞİ","DİNAZOR","MİLYON");
        Tabuu tabuu242 = new Tabuu("POKEMON","SARI","TOP","ÇİZGİ FİLM","ÇOCUKLUK","KANAL");
        Tabuu tabuu243 = new Tabuu("ATAKAN KAYALAR","INDIGO ÇOCUK","2020","ANNE","AKILLI","OLGUN");
        Tabuu tabuu244 = new Tabuu("KEHANET","BİLMEK","FAL","GELECEK","NOSTRADAMUS","KAHİN");
        Tabuu tabuu245 = new Tabuu("EKVATOR","COĞRAFYA","DÜNYA","AMAZON","SIFIR","EKSEN");
        Tabuu tabuu246 = new Tabuu("SEFAM OLSUN","YEDİRMEK","BÜLENT ERSOY","CIMBIZ","İÇİRMEK","DÜNYA");
        Tabuu tabuu247 = new Tabuu("MORG","HASTAHANE","CENAZE","ÖLÜM","KEFEN","HAK");
        Tabuu tabuu248 = new Tabuu("AMBLEM","LOGO","ETİKET","REKLAM","ŞEKİL","MARKA");
        Tabuu tabuu249 = new Tabuu("AĞDA YAPMAK","KIL","TÜY","ALMAK","BERBER","ERKEK");
        Tabuu tabuu250 = new Tabuu("SAYISAL LOTO","SÖZEL","TALİH KUŞU","İKRAMİYE","PARA","ŞANS");


        //Etkinlikler için veya market için aşağıdaki kelimeleri kullan

        Tabuu tabuu251 = new Tabuu("TABURE","OTURMAK","AYAKTA","PLASTİK","UCUZ","KIRILMAK");
        Tabuu tabuu252 = new Tabuu("CÜPPE","HOCA","MEZUNİYET","AHMET","KIYAFET","DİN");
        Tabuu tabuu253 = new Tabuu("FLÖRT ETMEK","SEVGİLİ","ARKADAŞ","KIZ","ERKEK","EVLENMEK");
        Tabuu tabuu254 = new Tabuu("NARSIST","BEĞENMEK","KİBİR","KASILMAK","BURNU BÜYÜK","SALDIRGAN");
        Tabuu tabuu255 = new Tabuu("UYUM","KARAKTER","EŞ","BENZER","DUYGU","DÜŞÜNCE");
        Tabuu tabuu256 = new Tabuu("AGRESİF","ATILGAN","SALDIRGAN","KÜSMEK","ÖFKELİ","SİNİRLİ");
        Tabuu tabuu257 = new Tabuu("İLAÇ","HAP","ŞURUP","HASTALIK","PSİKOLOJİK","DOKTOR");
        Tabuu tabuu258 = new Tabuu("NIKOLA TESLA","ELEKTRİK","ARABA","DAHİ","BİLİM","EDISON");
        Tabuu tabuu259 = new Tabuu("SANDIK","ESKİ","PARA","ALTIN","KOYMAK","GİZLİ");
        Tabuu tabuu260 = new Tabuu("ANIME","KORE","JAPON","ÇİZGİ DİZİ","GÖZ","YAŞ");
        Tabuu tabuu261 = new Tabuu("GOOGLE","ARAMAK","MOTOR","İNTERNET","YANDEX","YOUTUBE");
        Tabuu tabuu262 = new Tabuu("BREAKING BAD","DİZİ","KİMYA","SKYLER","HEISENBERG","WALTER WHITE");
        Tabuu tabuu263 = new Tabuu("DEMO","KISA","OYUN","FİLM","DİZİ","FRAGMAN");
        Tabuu tabuu264 = new Tabuu("ANDROID","İŞLETİM SİSTEMİ","GOOGLE","TELEFON","TEKNOLOJİ","WINDOWS");
        Tabuu tabuu265 = new Tabuu("KEMAL SUNAL","KOMİK","YEŞİLÇAM","ŞABAN","KOMEDİ","HABABAM SINIFI");
        Tabuu tabuu266 = new Tabuu("KABİNE","MECLİS","TOPLAMAK","SİYASİ","TOPLANTI","BAKAN");
        Tabuu tabuu267 = new Tabuu("AŞK","DUYGU","SEVGİLİ","ERKEK","KADIN","AYRILMAK");
        Tabuu tabuu268 = new Tabuu("GOLF","SPOR","SOPA","YEŞİL","ÇİMEN","ZENGİN");
        Tabuu tabuu269 = new Tabuu("DUBLÖR","ARTİST","SİNEMA","FİLM","DUBLAJ","KAMERA");
        Tabuu tabuu270 = new Tabuu("KORNEŞ","PERDE","CAM","PLASTİK","PENCERE","ODA");
        Tabuu tabuu271 = new Tabuu("AÇIK DÜNYA","OYUN","GTA","ROCKSTAR","CYBERPUNK","BİLGİSAYAR");
        Tabuu tabuu272 = new Tabuu("TELAFFUZ","SÖYLENİŞ","YABANCI","KONUŞMAK","DİL","AKSAN");
        Tabuu tabuu273 = new Tabuu("LOKUM","TÜRK","MUTFAK","TATLI","ŞEKER","BAYRAM");
        Tabuu tabuu274 = new Tabuu("ŞARTEL","ELEKTRİK","EV","TRAFO","PİL","TORNAVİDA");
        Tabuu tabuu275 = new Tabuu("KARA DELİK","UZAY","BOŞLUK","NASA","DELİK","SİYAH");
        Tabuu tabuu276 = new Tabuu("BLENDER","YEMEK","KIYMAK","KESMEK","MUTFAK","DİLİM");
        Tabuu tabuu277 = new Tabuu("MOBİLYA","ÇEKYAT","KOLTUK","BEYAZ EŞYA","KANEPE","YATMAK");
        Tabuu tabuu278 = new Tabuu("TAKVİM","MİLADİ","AY","GÜN","YIL","HAFTA");
        Tabuu tabuu279 = new Tabuu("SAHABE","PEYGAMBER","ARAP","ÇÖL","DİN","İSLAM");
        Tabuu tabuu280 = new Tabuu("ASTRONOMİ","ASTRONOT","UZAY","MARS","GEZEGEN","YILDIZ");
        Tabuu tabuu281 = new Tabuu("RADYASYON","KAKTÜS","ÇÖL","ELEKTRONİK","ÇERNOBİL","ATOM");
        Tabuu tabuu282 = new Tabuu("VAMPİR","KAN","ALACAKARANLIK","EMMEK","FİLM","KURT");
        Tabuu tabuu283 = new Tabuu("WHATSAPP","FACEBOOK","MARK ZUCKERBERG","GİZLİLİK","BİP","SIGNAL");
        Tabuu tabuu284 = new Tabuu("PİRİ REİS","HARİTA","GEMİ","OSMANLI","DONANMA","AVRUPA");
        Tabuu tabuu285 = new Tabuu("DEJAVU","YAŞAMAK","ÖNCE","GEÇMEK","RÜYA","OLAY");
        Tabuu tabuu286 = new Tabuu("GARDOLAP","ÇEKMECE","YATAK ODASI","KONSOL","ASMAK","AYNA");
        Tabuu tabuu287 = new Tabuu("STRATOSFER","BOŞLUK","ATMOSFER","MEZOSFER","COĞRAFİ","ASTRONOMİ");
        Tabuu tabuu288 = new Tabuu("MERİDYEN","ENLEM","BOYLAM","PARALEL","DÜNYA","SAAT");
        Tabuu tabuu289 = new Tabuu("AŞURE","KOMŞU","GÜN","BAYRAM","SEBZE","MEYVE");
        Tabuu tabuu290 = new Tabuu("KARINCA","HAYVAN","ATOM","BÖCEK","ÇALIŞKAN","ÇİZGİ FİLM");
        Tabuu tabuu291 = new Tabuu("MİSAK-I MİLLİ","YEMİN","SINIR","İNKILAP","MİLLİ","TARİH");
        Tabuu tabuu292 = new Tabuu("KAPİTÜLASYON","AYRICALIK","EKONOMİ","MİSAK-I MİLLİ","MALİ","OSMANLI");
        Tabuu tabuu293 = new Tabuu("GÜÇLER BİRLİĞİ","YASAMA","YÜRÜTME","YARGI","CUMHURİYET","KUVVET");
        Tabuu tabuu294 = new Tabuu("ROBOT","ELEKTRONİK","ANDROID","YAPAY ZEKA","YAZILIM","DÜŞMAN");
        Tabuu tabuu295 = new Tabuu("İPUCU","AJAN","DEDEKTİF","ARAMAK","POLİS","DELİL");
        Tabuu tabuu296 = new Tabuu("TAHSİLAT","ALACAK","PARA","ÖDEMEK","FATURA","BELGE");
        Tabuu tabuu297 = new Tabuu("SINAV","YAZILI","TEST","OKUL","AKADEMİSYEN","ÖĞRETMEN");
        Tabuu tabuu298 = new Tabuu("İKLİM","COĞRAFİ","AKDENİZ","DOĞA","ÇEVRE","MEVSİM");
        Tabuu tabuu299 = new Tabuu("PIRASA","SEBZE","UZUN","SAP","İNCE","ZEYTİN YAĞI");
        Tabuu tabuu300 = new Tabuu("BIYIK","SAKAL","ERKEK","TÜY","KÖSE","ADAM");


        Tabuu tabuu301 = new Tabuu("TRANSFER","PARA","FUTBOL","BASKETBOL","OYUNCU","MAÇ");
        Tabuu tabuu302 = new Tabuu("JUSTIN BIEBER","BABY","ŞARKICI","HAILEY BALDWIN","YUMMY","NEVER SAY NEVER");
        Tabuu tabuu303 = new Tabuu("DİKİŞ","TERZİ","GİYSİ","ETEK","MAKİNA","ALET");
        Tabuu tabuu304 = new Tabuu("RÜYA","BİLİNÇ","TABİR","HOCA","HZ. YUSUF","UYKU");
        Tabuu tabuu305 = new Tabuu("EMMANUEL MACRON","FRANSA","CUMHURBAŞKANI","BRIGITTE MACRON","GÖZ","NICOLAS SARKOZY");
        Tabuu tabuu306 = new Tabuu("LA CASE DE PAPEL","SUÇ","İSPANYOL","DENVER","NAIROBI","PROFESÖR");
        Tabuu tabuu307 = new Tabuu("ÜNİVERSİTE","EĞİTİM","OKUL","YÜKSEK","AKADEMİ","LİSANS");
        Tabuu tabuu308 = new Tabuu("MERKEZ","ANA","ALIŞVERİŞ","YOL","İLÇE","BİNA");
        Tabuu tabuu309 = new Tabuu("SÜMÜK","MENDİL","GRİP","AKMAK","BURUN","MUKOZA");
        Tabuu tabuu310 = new Tabuu("KORSAN","CD","DVD","KARAYİP","FİLM","OYUN");
        Tabuu tabuu311 = new Tabuu("GÜZELLİK","KADIN","KIZ","KARİZMATİK","BAYAN","RENKLİ");
        Tabuu tabuu312 = new Tabuu("AFRO","SAÇ","AFRİKA","AMERİKAN","SİYAHİ","IRK");
        Tabuu tabuu313 = new Tabuu("TÖRE","GELENEK","KURAL","KANUN","DOĞU","BAŞLIK PARASI");
        Tabuu tabuu314 = new Tabuu("YORGAN","BATTANİYE","YASTIK","YATMAK","GECE","YATAK");
        Tabuu tabuu315 = new Tabuu("AŞI","VİRÜS","KORONA","INFLUENZA","SALGIN","GRİP");
        Tabuu tabuu316 = new Tabuu("BAKLA","BİTKİ","ÇİÇEK","FASULYE","TANE","SEBZE");
        Tabuu tabuu317 = new Tabuu("TEŞVİK","ÖZENDİRMEK","TAVSİYE","ÖĞÜT","ETMEK","GİRMEK");
        Tabuu tabuu318 = new Tabuu("HAFIZA","TEKNİK","KALICI","ZEKİ","BİLGİ","DERS");
        Tabuu tabuu319 = new Tabuu("ATM","PARA","BANKA","ZİRAAT","ÇEKMEK","HALKBANK");
        Tabuu tabuu320 = new Tabuu("PARMAK","UZUV","ORGAN","AYAK","EL","TIRNAK");
        Tabuu tabuu321 = new Tabuu("ÇAKI","BİÇAK","KESKİN","KESİCİ","SERSERİ","İTALYAN");
        Tabuu tabuu322 = new Tabuu("KÜLLÜK","İÇMEK","ERKEK","SİGARA","KÜL","TÜTÜN");
        Tabuu tabuu323 = new Tabuu("LEKE","İZ","SİVİLCE","AKNE","BEN","YÜZ");
        Tabuu tabuu324 = new Tabuu("KARTEL","UYUŞTURUCU","MEKSİKA","SINALOA","EL-CHAPO","ÇATIŞMA");
        Tabuu tabuu325 = new Tabuu("SAAT","ZAMAN","GEÇMEK","FİZİK","DAKİKA","SANİYE");
        Tabuu tabuu326 = new Tabuu("SÜNGER BOB","ÇİZGİ","FİLM","SARI","ÇOCUK","KARAKTER");
        Tabuu tabuu327 = new Tabuu("AFACAN","ÇOCUK","AZMAK","OYUN","7","YARAMAZ");
        Tabuu tabuu328 = new Tabuu("YUZARSİF","FİLM","ARAP","YUSUF","PEYGAMBER","GÜZELLİK");
        Tabuu tabuu329 = new Tabuu("OPERATÖR","HAT","SIM","KART","TELEFON","TURKCELL");
        Tabuu tabuu330 = new Tabuu("MINECRAFT","BLOK","VİDEO OYUNU","ÇOCUK","ZOMBİ","MICROSOFT");
        Tabuu tabuu331 = new Tabuu("PUBG","EKİP","TAKIM","SİLAH","SERBEST","MOTOR");
        Tabuu tabuu332 = new Tabuu("TWITCH","CANLI","YAYIN","YOU NOW","YAYINCI","OYUN");
        Tabuu tabuu333 = new Tabuu("CİLT BAKIMI","ECZANE","GÜZELLİK","PUDRA","SİVİLCE","KADIN");
        Tabuu tabuu334 = new Tabuu("ANTİBİYOTİK","İLAÇ","HAP","KANSER","AĞIR","BÜYÜK");
        Tabuu tabuu335 = new Tabuu("TAVİYE","EK","GIDA","YANINDA","BESİN","VİTAMİN");
        Tabuu tabuu336 = new Tabuu("KİLO","GENİŞ","AĞIR","VÜCUT","İNSAN","YEMEK");
        Tabuu tabuu337 = new Tabuu("HİJYEN","TEMİZLİK","TİTİZLİK","ANTİBAKTERİ","SABUN","DETERJAN");
        Tabuu tabuu338 = new Tabuu("FACEBOOK","INSTAGRAM","TWITTER","SOSYAL MEDYA","WHATSAPP","TOPLULUK");
        Tabuu tabuu339 = new Tabuu("UYUŞTURMAK","İĞNE","İLAÇ","AŞI","SIKMAK","UYUŞTURUCU");
        Tabuu tabuu340 = new Tabuu("ÇAĞ","YÜZYIL","GEÇMİŞ","GELECEK","KEBAP","ERZURUM");
        Tabuu tabuu341 = new Tabuu("EKMEK","NAN","NAN-I AZİZ","SOMUN","ODUN","LİVA");
        Tabuu tabuu342 = new Tabuu("TANDIR","SİMİT","POĞAÇA","SICAK","EKMEK","KAHVALTI");
        Tabuu tabuu343 = new Tabuu("TARZ","MODA","GİYSİ","UYUM","OLMAK","YAKIŞIKLI");
        Tabuu tabuu344 = new Tabuu("BERKAY HARDAL","OYUNCU","BEYAZ","DİLAN TELKÖK","MELEKLERİN AŞKI","İSTANBULLU GELİN");
        Tabuu tabuu345 = new Tabuu("TERMİNATÖR","YOKEDİCİ","KARAKTER","FİLM","KAS","BİLİM KURGU");
        Tabuu tabuu346 = new Tabuu("HÖRGÜÇ","DEVE","ÇÖL","KAMBUR","BİNEK","SU");
        Tabuu tabuu347 = new Tabuu("BEBEK","ANNE","SÜT","EMZİK","ÇOCUK","ARABASI");
        Tabuu tabuu348 = new Tabuu("ÖRNEK","NUMUNE","BENZER","MODEL","AYNI","DANTEL");
        Tabuu tabuu349 = new Tabuu("POLAT ALEMDAR","KURTLAR VADİSİ PUSU","FİLİSTİN","ÖLMEZ","ESKİ","MEMATİ");
        Tabuu tabuu350 = new Tabuu("KANSER","HASTALIK","KÖTÜ","TÜMÖR","GEÇMEZ","ZOR");
        Tabuu tabuu351 = new Tabuu("NETFLIX","DİZİ","FİLM","DUBLAJ","SEZON","GİRİŞİM");
        Tabuu tabuu352 = new Tabuu("STEAM","OYUN","PLATFORM","GABE NEWELL","GİRİŞİM","DEMO");
        Tabuu tabuu353 = new Tabuu("AİLE","GENİŞ","ÇEKİRDEK","KARDEŞ","ANNE","BABA");
        Tabuu tabuu354 = new Tabuu("DOĞRULAMAK","E-POSTA","TELEFON","GÜVENLİK","BİLGİ","DERLEMEK");
        Tabuu tabuu355 = new Tabuu("RADYO","TELEFON","ÇALAR","ARABA","İLAHİ","MÜZİK");
        Tabuu tabuu356 = new Tabuu("PLAZMA","TELEVİZYON","LCD","EKRAN","İNÇ","ELEKTRONİK");
        Tabuu tabuu357 = new Tabuu("İLETİŞİM","CONTACT","REHBER","MESAJ","E-POSTA","ÖZEL");
        Tabuu tabuu358 = new Tabuu("MAĞAZA","MERKEZ","MARKET","TELEFON","MARKA","OYUN");
        Tabuu tabuu359 = new Tabuu("AVUKAT","HAKİM","MAHKEME","SAVUNMAK","KATİL","CEZA");
        Tabuu tabuu360 = new Tabuu("EVLENMEK","KARI - KOCA","VALİ","BELGE","NİŞAN","DÜĞÜN");
        Tabuu tabuu361 = new Tabuu("ÜREME","HAYVAN","DOĞURMAK","YAVRU","ÇİFT","ÇOĞALMAK");
        Tabuu tabuu362 = new Tabuu("NÜKLEOTİD","FOSFAT","ORGANİK","BAZ","DNA","BİRİM");
        Tabuu tabuu363 = new Tabuu("KROMOZOM","ENGELLİ","DOWN","SENDROM","44","41");
        Tabuu tabuu364 = new Tabuu("SAMANYOLU","GALAKSİ","ASTRONOM","GEZEGEN","YILDIZ","BOŞLUK");
        Tabuu tabuu365 = new Tabuu("ADA","YARIM","SURVIVOR","ACUN ILICALI","MASTERCHEF","GİRİŞİMCİ");
        Tabuu tabuu366 = new Tabuu("AFİFE JALE","YILDIZ","TİYATROCU","ESKİ","OYUNCU","ADAM");
        Tabuu tabuu367 = new Tabuu("TANTUNİ","DÜRÜM","DÖNER","TAVUK","ÇİĞKÖFTE","HAMUR");
        Tabuu tabuu368 = new Tabuu("SENSÖR","ARABA","ALARM","ÇALMAK","İLERİ","GERİ");
        Tabuu tabuu369 = new Tabuu("DALAK","ŞİŞMEK","HERKES","AĞRIMAK","MİDE","KAN");
        Tabuu tabuu370 = new Tabuu("MİSAFİRPERVER","YEMEK","KONUK","KONUT","AĞIRLAMAK","ÇAĞIRMAK");
        Tabuu tabuu371 = new Tabuu("MOLA","VERMEK","DAKİKA","SERBEST","TENEFÜS","ZİL");
        Tabuu tabuu372 = new Tabuu("FATURA","SU","DOĞALGAZ","RUSYA","ELEKTRİK","AY");
        Tabuu tabuu373 = new Tabuu("EMANET","ALLAH","GÜLE GÜLE","GÖRÜŞMEK","OLMAK","GİTMEK");
        Tabuu tabuu374 = new Tabuu("İKRAM","BEDAVA","PARASIZ","ŞEKER","MİSAFİR","LOKUM");
        Tabuu tabuu375 = new Tabuu("REKLAM","UYGULAMA","TELEVİZYON","ARA VERMEK","AZ","PROGRAM");
        Tabuu tabuu376 = new Tabuu("OTOPSİ","ADLİ TIP","KADAVRA","CERRAH","AMELİYAT","CESET");
        Tabuu tabuu377 = new Tabuu("ÖMER HAYYAM","ŞARAP","ŞAİR","RUBAİ","ŞİİR","FARS");
        Tabuu tabuu378 = new Tabuu("ZONKLAMAK","VURMAK","BEYİN","AĞRIMAK","ŞAKAK","KEMİK");
        Tabuu tabuu379 = new Tabuu("ROMATİZM","JEST","SEVGİLİ","MİMİK","ERKEK","BAYAN");
        Tabuu tabuu380 = new Tabuu("HAFRİYAT","ŞANTİYE","BİNA","KATO","TOPRAK","KAZMAK");
        Tabuu tabuu381 = new Tabuu("MAHREM","BAYAN","ERKEK","GÜNAH","AÇIK","KADIN");
        Tabuu tabuu382 = new Tabuu("APSE","İLTİHAP","KEMİK","EZİLMEK","DİŞ","SIVI");
        Tabuu tabuu383 = new Tabuu("HİCRET","ARAP","DİN","İSLAM","MEKKE","KABE");
        Tabuu tabuu384 = new Tabuu("UÇ","0.5","İNCE","KIRTASİYE","KALEM","0.7");
        Tabuu tabuu385 = new Tabuu("SEÇENEK","İSTEK","BAĞLI","OLMAK","YAPMAK","ŞIK");
        Tabuu tabuu386 = new Tabuu("KORNEA","GÖRMEK","GÖZ","GÖZLÜK","LENS","TABAKA");
        Tabuu tabuu387 = new Tabuu("SPOR","KOŞU","FITNESS","KAS","SAĞLIK","MUTLULUK");
        Tabuu tabuu388 = new Tabuu("İTFAİYE","YANGIN","EKİP","SU","FİŞKIRTMAK","EV");
        Tabuu tabuu389 = new Tabuu("HALTER","AĞIRLIK","NAİM SÜLEYMANOĞLU","ESKİ","KAS","SPOR");
        Tabuu tabuu390 = new Tabuu("GÖKYÜZÜ","SATÜRN","ASTRONOMİ","MARS","DÜNYA","UZAY");
        Tabuu tabuu391 = new Tabuu("HACİVAT","KARAGÖZ","GELENEK","TİYATRO","OYUN","CAHİL");
        Tabuu tabuu392 = new Tabuu("YASAK","OYUN","HAPİS","ÇIKMAK","CEZA","EV");
        Tabuu tabuu393 = new Tabuu("KABLO","BAKIR","TEL","İLETKEN","ELEKTRONİK","ELEKTRİK");
        Tabuu tabuu394 = new Tabuu("CESUR","BABAYİĞİT","KUVVETLİ","KORKMAK","ATILGAN","DÖVMEK");
        Tabuu tabuu395 = new Tabuu("KAPTAN","TAKIM","SÜRÜCÜ","KILAVUZ","GEMİ","PİLOT");
        Tabuu tabuu396 = new Tabuu("İHRACAT","REKOR","TİCARET","İTHALAT","DIŞ","ÜLKE");
        Tabuu tabuu397 = new Tabuu("KAMU","DEVLET","HALK","BAŞKAN","MUHTAR","MAL");
        Tabuu tabuu398 = new Tabuu("BAMYA","YEMEK","SEBZE","YAPMAK","BESİN","YEŞİL");
        Tabuu tabuu399 = new Tabuu("BİBER","ACI","TATLI","İSOT","URFA","KIRMIZI");
        Tabuu tabuu400 = new Tabuu("ENSTRUMENTAL","ÇALMAK","MÜZİK","GİTAR","ÇALMAK","ALET");

        //300 tabuu kartı için

        Tabuu tabuu401 = new Tabuu("CİHAZ","ÜRÜN","MAKİNE","KÜÇÜK","ELEKTRONİK","APARAT");
        Tabuu tabuu402 = new Tabuu("İHTİYAÇ","VAR","ZORUNLU","YEMEK","SU","YAŞAM");
        Tabuu tabuu403 = new Tabuu("ANTROPOMETRİ","ÖLÇÜ","İNSAN","UZUNLUK","BOYUT","BEDEN");
        Tabuu tabuu404 = new Tabuu("REAKTÖR","URANYUM","NÜKLEER","RADYASYON","SANTRAL","ÇERNOBİL");
        Tabuu tabuu405 = new Tabuu("PETROL","BENZİN","YAKIT","MAZOT","AKARYAKIT","OTOMOBİL");
        Tabuu tabuu406 = new Tabuu("BETON","ÇİMENTO","İNŞAAT","GRİ","DUVAR","YAPMAK");
        Tabuu tabuu407 = new Tabuu("İLETİŞİM","HABERLEŞME","TELEFON","MESAJ","TEKNOLOJİ","OPERATÖR");
        Tabuu tabuu408 = new Tabuu("TAMİR","ETMEK","ONARMAK","BOZULMAK","KIRILMAK","USTA");
        Tabuu tabuu409 = new Tabuu("GÜVENLİK","KORUNMAK","TEHLİKE","İŞ","YASA","GÖREVLİ");
        Tabuu tabuu410 = new Tabuu("JEOTERMAL","SU","BUHAR","SICAK","YER ALTI","MAGMA");
        Tabuu tabuu411 = new Tabuu("IŞIK","ENERJİ","AMPÜL","LAMBA","AYDINLATMAK","LED");
        Tabuu tabuu412 = new Tabuu("DALGA","DENİZ","ENERJİ","OKYANUS","SU","SÖRF");
        Tabuu tabuu413 = new Tabuu("ZEMİN","YER","MİMARİ","TEMEL","FAYANS","KAT");
        Tabuu tabuu414 = new Tabuu("ÇATI","EV","KORUMAK","KİREMİT","KAT","ÇEŞİT");
        Tabuu tabuu415 = new Tabuu("DUVAR","BETON","YALITIM","PENCERE","TUĞLA","BAHÇE");
        Tabuu tabuu416 = new Tabuu("ARAŞTIRMAK","İNTERNET","WIKIPEDIA","DEDEKTİF","BULMAK","SONUÇ");
        Tabuu tabuu417 = new Tabuu("KİMYASAL","TEPKİME","FEN","ENERJİ","SIVI","DENEY");
        Tabuu tabuu418 = new Tabuu("DÖNÜŞÜM","ENERJİ","SANTRAL","DÖNMEK","YENİ","ELEKTRİK");
        Tabuu tabuu419 = new Tabuu("PAYLAŞMAK","ORTAK","HİSSEDAR","YARDIM","FAKİR","ZENGİN");
        Tabuu tabuu420 = new Tabuu("TOPLUMSAL","İNSAN","KURAL","HALK","MİLLET","SORUN");
        Tabuu tabuu421 = new Tabuu("DAYANIŞMA","BİRLİK","BERABERLİK","YARDIMLAŞMA","DESTEK","ZORLUK");
        Tabuu tabuu422 = new Tabuu("SADAKA","YARDIM","PARA","ÖMÜR","DİLENCİ","FAKİR");
        Tabuu tabuu423 = new Tabuu("YARDIMLAŞMA KURUMU","PARA","KIZILAY","YEŞİLAY","DOĞAL AFET","MADDİ/MANEVİ");
        Tabuu tabuu424 = new Tabuu("SADAKA-İ CARİYE","YARDIM","SÜREKLİ","ÇEŞME","HASTANE","OKUL");
        Tabuu tabuu425 = new Tabuu("TAVLA","ZAR","OYUN","YENMEK","MARS","KAPI");
        Tabuu tabuu426 = new Tabuu("OKEY","ZAR","ISTAKA","TAŞ","4 KİŞİ","OYUN");
        Tabuu tabuu427 = new Tabuu("ÇORAP","İNCE","KAÇMAK","TEN RENGİ","PARİZYEN","AYAK");
        Tabuu tabuu428 = new Tabuu("MÜCEVHER","KADIN","TAKI","ALTIN","BİLEZİK","KOLYE");
        Tabuu tabuu429 = new Tabuu("OJE","TIRNAK","RENK","SÜRMEK","ASETON","KIRMIZI");
        Tabuu tabuu430 = new Tabuu("ŞİMŞEK","GÖKYÜZÜ","YAĞMUR","IŞIK","KALP KRİZİ","BULUT");
        Tabuu tabuu431 = new Tabuu("STETESKOP","SES","KALP","DOKTOR","BOYUN","BÜYÜTEÇ");
        Tabuu tabuu432 = new Tabuu("HEMŞİRE","HASTALIK","HASTANE","DOKTOR","İLK YARDIM","SAĞLIK");
        Tabuu tabuu433 = new Tabuu("MECLİS HÜKÜMETİ","MİLLETVEKİLİ","TBMM","YÖNETMEN","YÖNETİM","İRADE");
        Tabuu tabuu434 = new Tabuu("ÇALIŞMAK","ANNE","BABA","İŞ","PARA","DERS");
        Tabuu tabuu435 = new Tabuu("SARILMAK","ANNE","BABA","SEVGİ","ÇOCUK","AÇMAK");
        Tabuu tabuu436 = new Tabuu("MAYIS","ANNE","ÇOCUK","KUTLAMA","DÜNYA","AY");
        Tabuu tabuu437 = new Tabuu("ANNELER GÜNÜ","ÇİÇEK","HEDİYE","ÇOCUK","TEBRİK","SARILMAK");
        Tabuu tabuu438 = new Tabuu("GEN","GÖREV BİRİMİ","DNA","ÖZELLİK","NÜKLEOTİD","ŞİFRE");
        Tabuu tabuu439 = new Tabuu("CİNSİYET","KIZ","ERKEK","X KROMOZOMU","Y KROMOZOMU","BABA");
        Tabuu tabuu440 = new Tabuu("SINAV","DERS","PUAN","OKUL","SINIF","GİRMEK");
        Tabuu tabuu441 = new Tabuu("RİTİM","MÜZİK","TEMPO","TASARIM","KONU","RESİM");
        Tabuu tabuu442 = new Tabuu("SES","TEL","ENERJİ","MÜZİK","ÇIKMAK","KONUŞMAK");
        Tabuu tabuu443 = new Tabuu("GRAFİK","GÖRSEL","TASARIM","KONU","PASTA","ÇİZİM");
        Tabuu tabuu444 = new Tabuu("ÇEVRE","KİRLİLİK","DOĞA","DÜNYA","ÇÖP","ETRAF");
        Tabuu tabuu445 = new Tabuu("KARBON","ATOM","MONOKSİT","ZARAR","FABRİKA","BACA");
        Tabuu tabuu446 = new Tabuu("İKLİM","DOĞA","ÇEVRE","MEVSİM","DEĞİŞİKLİK","BÖLGE");
        Tabuu tabuu447 = new Tabuu("TEMEL","MİMARİ","İNŞAAT","EV","TAŞIYICI","ELEMAN");
        Tabuu tabuu448 = new Tabuu("FİSYON","URANYUM","ATOM","TEPKİME","RADYASYON","PARÇALANMA");
        Tabuu tabuu449 = new Tabuu("ARAÇ","MALZEME","GEREÇ","İŞ","DERS","KULLANMAK");
        Tabuu tabuu450 = new Tabuu("İCAT","YENİLİK","MUCİT","BULMAK","OLMAYAN","ÇIKARMAK");
        Tabuu tabuu451 = new Tabuu("PANEL","KONFERANS","GÜNEŞ","ENERJİ","ELEKTRİK","YENİLENEBİLİR");
        Tabuu tabuu452 = new Tabuu("SERGİ","DÖNEM","OKUL","FUAR","SUNMAK","DERS");
        Tabuu tabuu453 = new Tabuu("ÜRETİM","FABRİKA","EL","MAKİNE","SANAYİ","SERİ");
        Tabuu tabuu454 = new Tabuu("REKLAM","TELEVİZYON","SATIŞ","PARA","YAPMAK","UYGULAMA");
        Tabuu tabuu455 = new Tabuu("İNOVASYON","YENİLİK","YENİLEŞİM","TASARIM","ÜRÜN","DÜŞÜNME");
        Tabuu tabuu456 = new Tabuu("BİLİM","İNSAN","ARAŞTIRMA","DENEY","LABORATUVAR","YENİ");
        Tabuu tabuu457 = new Tabuu("MUTLAK DEĞER","POZİTİF","EKSİ","SAYI","UZAKLIK","MATEMATİK");
        Tabuu tabuu458 = new Tabuu("ORİJİN","SIFIR","NOKTA","X EŞİTTİR Y","KOORDİNAT","MATEMATİK");
        Tabuu tabuu459 = new Tabuu("FAİZ","HARAM","OLMAK","BANKA","AYLIK","ATM");
        Tabuu tabuu460 = new Tabuu("ENFLASYON","EKONOMİ","ARTIŞ","MAL","ÜRÜN","ÜLKE");
        Tabuu tabuu461 = new Tabuu("YARI ÇAP","ÇEMBER","R","DAİRE","MERKEZ","ÇEVRE");
        Tabuu tabuu462 = new Tabuu("ZARAR","KAR","ALIŞ","SATIŞ","MAL","YÜZDE");
        Tabuu tabuu463 = new Tabuu("ROBOT","KENDİ","MAKİNE","YAPAY ZEKA","FABRİKA","SOPHİA");
        Tabuu tabuu464 = new Tabuu("HAYAT","YAŞAM","İNSAN","ÖMÜR","ZAMAN","GÜZEL");
        Tabuu tabuu465 = new Tabuu("MAKİNE","ÜRETİM","KAS GÜCÜ","HIZLI","KOLAY","ELEKTRİK");
        Tabuu tabuu466 = new Tabuu("TUHAFİYE","ELBİSE","OKUL","GİYSİ","KUMAŞ","ETEK");
        Tabuu tabuu467 = new Tabuu("TÜBİTAK","SERGİ","FUAR","BİLİM","İCAT","YAPMAK");
        Tabuu tabuu468 = new Tabuu("PROJE","NOT","PUAN","KONTROL","ÜRETİM","TASARIM");
        Tabuu tabuu469 = new Tabuu("ATÖLYE","DERS","TASARIM","TEKNOLOJİ","İŞ","BİLGİ");
        Tabuu tabuu470 = new Tabuu("CETVEL","ÖLÇÜM","MATEMATİK","UZUNLUK","BOY","ÇİZİM");
        Tabuu tabuu471 = new Tabuu("MAKAS","KAĞIT","KESMEK","KUMAŞ","TASARIM","ARAÇ");
        Tabuu tabuu472 = new Tabuu("PAZARLAMA","SATIŞ","PARA","ÜRÜN","MÜŞTERİ","MAL");
        Tabuu tabuu473 = new Tabuu("HİDRO ELEKTRİK SANTRALİ","ENERJİ","BARAJ","ATATÜRK","SU","TÜRBİN");
        Tabuu tabuu474 = new Tabuu("EDİSON","AMPÜL","ELEKTRİK","EINSTEIN","NIKOLA","IŞIK");
        Tabuu tabuu475 = new Tabuu("MOTOR","PİSTON","YAKIT","ARAÇ","TAŞIT","BİSİKLET");
        Tabuu tabuu476 = new Tabuu("ERGONOMİ","RAHAT","GÜVENLİ","SAĞLIKLI","ANTROPOMETRİ","OTURMAK");
        Tabuu tabuu477 = new Tabuu("TASARIM","DIŞ GÖRÜNÜM","FİKİR","KURGU","RENK","ÜRÜN");
        Tabuu tabuu478 = new Tabuu("TEKNOLOJİ","ÜRÜN","BİLGİSAYAR","ÜRETMEK","KOLAY","YENİ");
        Tabuu tabuu479 = new Tabuu("BULUŞ","İCAT","BİLİM","İNSAN","YENİ","YÜZYIL");
        Tabuu tabuu480 = new Tabuu("YALITIM","SOĞUK","SICAK","TERMOS","DUVAR","CEPHE");
        Tabuu tabuu481 = new Tabuu("KULLANIŞLI","ÜRÜN","PRATİK","KÜÇÜK","TASARIM","EV");
        Tabuu tabuu482 = new Tabuu("UYGUN","GÖRE","TAM","ÖLÇÜM","EVRE","GİYMEK");
        Tabuu tabuu483 = new Tabuu("BİÇİM","TASARIM","ARAÇ","ÇİZİM","KOMPOZİSYON","TEMEL");
        Tabuu tabuu484 = new Tabuu("GÖLGE","GÜNEŞ","IŞIK","YANSIMA","KARANLIK","SİYAH");
        Tabuu tabuu485 = new Tabuu("BİYOKÜTLE","ENERJİ","YAĞ","GERİ DÖNÜŞÜM","YENİLENEBİLİR","SIVI");
        Tabuu tabuu486 = new Tabuu("YAPI","MİMARİ","İNŞAAT","TASARIM","YAPMAK","KONUT");
        Tabuu tabuu487 = new Tabuu("KOMPOZİSYON","YAZMAK","ÇİZİM","DERS","ANLATMAK","SONUÇ");
        Tabuu tabuu488 = new Tabuu("ESTETİK","GÜZEL","ŞIK","TARZ","YAPTIRMAK","ÖZELLİK");
        Tabuu tabuu489 = new Tabuu("DEFTERDAR","DİVAN","MALİYE","ANADOLU","RUMELİ","MAL");
        Tabuu tabuu490 = new Tabuu("MATBAA","28 MEHMET","MÜTEFERRİKA","KİTAP","LALE DEVRİ","FOTOKOPİ");
        Tabuu tabuu491 = new Tabuu("PİRİ REİS","HARİTA","DENİZ","OSMANLI","DONANMA","İMPARATORLUK");
        Tabuu tabuu492 = new Tabuu("LALE DEVRİ","PATRONA HALİL","İSYAN","LALE","ÇİÇEK","3. AHMET");
        Tabuu tabuu493 = new Tabuu("ŞAHİ TOPU","1453","İSTANBUL","FATİH","BİZANS","TOPKAPI");
        Tabuu tabuu494 = new Tabuu("AYASOFYA","AÇMAK","AK PARTİ","İBADET","YUNAN","AVRUPA");
        Tabuu tabuu495 = new Tabuu("CAM","SODA","TOPRAK","PENCERE","FENİKELİLER","SAYDAM");
        Tabuu tabuu496 = new Tabuu("NÜFUS","İNSAN","GÖÇ","SATIM","MEVCUT","RAKIM");
        Tabuu tabuu497 = new Tabuu("SEYAHATNAME","EVLİYA ÇELEBİ","GEZİ","TATİL","YAZAR","SEYEHAT");
        Tabuu tabuu498 = new Tabuu("RÖNESANS","BİLİM","GALILEO","YENİDEN","DOĞUŞ","AVRUPA");
        Tabuu tabuu499 = new Tabuu("MONA LİSA","TABLO","DA VİNCİ","İTALYA","RÖNESANS","AVRUPA");
        Tabuu tabuu500 = new Tabuu("GÜNEŞ TAKVİMİ","GÜNEŞ","MISIR","MİLADİ","ZAMAN","YIL");
        Tabuu tabuu501 = new Tabuu("NÜFUS","SAYIM","2. MAHMUT","ASKER","VERGİ","DEVLET");
        Tabuu tabuu502 = new Tabuu("PAPİRÜS","BİTKİ","YAZI","KAĞIT","MISIR","YAPRAK");
        Tabuu tabuu503 = new Tabuu("KIRKPINAR","GÜNEŞ","SPOR","ER MEYDANI","PEHLİVAN","AY");
        Tabuu tabuu504 = new Tabuu("İCAT","BULUŞ","GELİŞTİRMEK","BİLİM ADAMI","EDISON","BULMAK");
        Tabuu tabuu505 = new Tabuu("ÇİMPE KALESİ","AVRUPA","BİZANS","OSMANLI","OSMAN BEY","ORHAN BEY");
        Tabuu tabuu506 = new Tabuu("YÖRÜNGE","DÜNYA","UZAY GEMİSİ","GİRMEK","YOL","TAKİP ETMEK");
        Tabuu tabuu507 = new Tabuu("DOKUZ CANLI","ÖLMEK","DÜŞMEK","DAYANMAK","CESUR","ÖLÜMSÜZ");
        Tabuu tabuu508 = new Tabuu("TABURCU OLMAK","İYİLEŞMEK","HASTA","CANLI","YATMAK","HASTAHANE");
        Tabuu tabuu509 = new Tabuu("NEFİS","İRADE","İSTEMEK","GİDERMEK","YEMEK","YATIŞTIRMAK");
        Tabuu tabuu510 = new Tabuu("KUZUKULAĞI","SALATA","EKŞİ","OT","YAPRAK","ROKA");
        Tabuu tabuu511 = new Tabuu("UÇARI","KAÇARI","BAĞLI","SORUMLULUK","EĞLENMEK","GEZMEK");
        Tabuu tabuu512 = new Tabuu("AĞDALI","AĞIR","TATLI","DİL","KARMAŞIK","ŞERBET");
        Tabuu tabuu513 = new Tabuu("ÇALAKALEM","YAZMAK","ÖZENTİ","BAŞTAN SAVMAK","NOT","ACELE");
        Tabuu tabuu514 = new Tabuu("ANTİKA","EŞYA","ESKİ","TARİH","EV","KLASİK");
        Tabuu tabuu515 = new Tabuu("CİNGÖZ","AKILLI","ZEKİ","UYANIK","KURNAZ","ÇIKARI OLMAK");
        Tabuu tabuu516 = new Tabuu("AMBİYANS","ORTAM","HAVA","GÜZEL","MEKAN","MALİKANE");
        Tabuu tabuu517 = new Tabuu("HENTBOL","SPOR","TOP","EL","DİREK","TAKIM");
        Tabuu tabuu518 = new Tabuu("KURU İFTİRA","ATMAK","SAKLAMAK","YALAN","GERÇEK","SUÇLAMAK");
        Tabuu tabuu519 = new Tabuu("TROMPET","MÜZİK","BANDO","NEFESLİ","ÜFLEMEK","SAKSAFON");
        Tabuu tabuu520 = new Tabuu("GÜNEŞ BANYOSU","YANMAK","KREM","YATMAK","BRONZ","KUM");
        Tabuu tabuu521 = new Tabuu("ŞARLATAN","ŞAKLABAN","DOLANDIRMAK","ÖVMEK","EĞLENDİRMEK","SOYTARI");
        Tabuu tabuu522 = new Tabuu("ANA KUZUSU","MUHALLEBİ","ANNE","BABA","BÜYÜTMEK","BAĞIRMAK");
        Tabuu tabuu523 = new Tabuu("ADRENALİN","HORMON","TRAFİK","ÖFKE","HEYECAN","KOŞMAK");
        Tabuu tabuu524 = new Tabuu("ORTAPEDİK","RAHAT","SAĞLIK","TABAN","ÖZEL","AYAKKABI");
        Tabuu tabuu525 = new Tabuu("ÇORBA","SU","YEMEK","TOZ","SIVI","DOMATES");
        Tabuu tabuu526 = new Tabuu("CÜSSE","KALIP","İRİ","YAPI","GENİŞ","KİLO");
        Tabuu tabuu527 = new Tabuu("KENAN SOFUOĞLU","YARIŞMA","MİLLETVEKİLİ","MOTOR","ŞAMPİYON","PİST");
        Tabuu tabuu528 = new Tabuu("MUAF TUTMAK","VERGİ","BULUNMAK","DEVLET","ÖDEMEK","GÖREV");
        Tabuu tabuu529 = new Tabuu("AKINTI","BURUN","BOĞAZ","YÜZMEK","KÜREK ÇEKMEK","GENİZ");
        Tabuu tabuu530 = new Tabuu("YARIM YAMALAK","BAŞTAN SAVMA","ÖZENSİZ","YAPMAK","EKSİK","BİTMEK");
        Tabuu tabuu531 = new Tabuu("AĞAÇKAKAN","KUŞ","GAGALAMAK","ORMAN","VURMAK","OYMAK");
        Tabuu tabuu532 = new Tabuu("DEFNE YAPRAĞI","AĞAÇ","BALIK","KOKMAK","SABUN","YEMEK");
        Tabuu tabuu533 = new Tabuu("ZEVZEK","BOŞ KONUŞMAK","SAÇMA SAPAN","GEVEZE","UĞRAŞMAK","ÇENESİ DÜŞÜK");
        Tabuu tabuu534 = new Tabuu("İDRAK ETMEK","ANLAMAK","AKIL ERMEK","KAVRAMAK","AKLI BAŞINA GELMEK","ALGILAMAK");
        Tabuu tabuu535 = new Tabuu("ÖZDEŞLEŞMEK","SEVMEK","AYNI","EŞİT","BENZER","AYRILMAK");
        Tabuu tabuu536 = new Tabuu("İLTİMAS","HAKSIZ","GEÇMEK","KAYIRMAK","TORPİL","AYRICALIK");
        Tabuu tabuu537 = new Tabuu("AHENK","UYUM","SES","DANS","ŞİİR","UZLAŞMA");
        Tabuu tabuu538 = new Tabuu("JÜPON","ETEK","ELBİSE","GİYMEK","KISA","İÇ");
        Tabuu tabuu539 = new Tabuu("CAZİBE","ÇEKİCİ","ALIMLI","KADIN","GÜZELLİK","ZARİF");
        Tabuu tabuu540 = new Tabuu("OYALAMAK","KONUŞMAK","ERTELEMEK","GECİKMEK","LAFA TUTMAK","GEREK");
        Tabuu tabuu541 = new Tabuu("ASMA KAT","TAVAN","MERDİVEN","DÜKKAN","ÜST","BİRİNCİ");
        Tabuu tabuu542 = new Tabuu("KUVER","EKMEK","RESTORAN","LOKANTA","HESAP","ÜCRET");
        Tabuu tabuu543 = new Tabuu("TİMSAH GÖZYAŞI","ÜZÜLMEK","AĞLAMAK","YALAN","İNANMAK","GERÇEK");
        Tabuu tabuu544 = new Tabuu("MÜTEMADİYEN","ZAMAN","ARA VERMEK","HEP","SÜRMEK","SIKLIK");
        Tabuu tabuu545 = new Tabuu("SIKBOĞAZ","ZORLAMAK","SIKIŞTIRMAK","SORMAK","BASKI YAPMAK","İSTEMEK");
        Tabuu tabuu546 = new Tabuu("KAMU SPOTU","BİLGİ","TELEVİZYON","REKLAM","DEVLET","YARDIM");
        Tabuu tabuu547 = new Tabuu("TURNUSOL KAĞIDI","ASİT","BAZ","AYIRMAK","LABORATUVAR","ANLAMAK");
        Tabuu tabuu548 = new Tabuu("SEFER TASI","ÖĞLE YEMEĞİ","TAŞIMAK","OKUL","KAP","BESLENME");
        Tabuu tabuu549 = new Tabuu("MİNDER","OTURMAK","YASLANMAK","ZEMİN","RAHATLIK","YASTIK");
        Tabuu tabuu550 = new Tabuu("MUKAYESE","KARŞILAŞTIRMAK","BENZETME","ARASINDA","KIYAS","ÇOCUK");
        Tabuu tabuu551 = new Tabuu("ERZAK","YEMEK","SEBZE","MEYVE","İHTİYAÇ","DEPO");
        Tabuu tabuu552 = new Tabuu("UYKULUK","SAKATAT","ET","KOKOREÇ","SÜTLÜCE","KUZU");
        Tabuu tabuu553 = new Tabuu("YEM TORBASI","BAĞLAMAK","AT","ACIKMAK","TAKMAK","BAŞ");
        Tabuu tabuu554 = new Tabuu("BAĞDAŞ KURMAK","BACAK","YOGA","OTURMAK","YER","AYAK");
        Tabuu tabuu555 = new Tabuu("GELİN ADAYI","EVLENMEK","AİLE","DÜĞÜN","DAMAT","KONVOY");
        Tabuu tabuu556 = new Tabuu("ÇEMKİRMEK","KONUŞMAK","BAĞIRMAK","KARŞI GELMEK","CEVAP VERMEK","ÇİRKEFLEŞMEK");
        Tabuu tabuu557 = new Tabuu("DALLI BUDAKLI","KARIŞIK","DERT","AĞAÇ","ÇETREFİL","BÜYÜMEK");
        Tabuu tabuu558 = new Tabuu("DEVRETMEK","AKTARMAK","DÜKKAN","İKRAMİYE","PİYANGO","LOTO");
        Tabuu tabuu559 = new Tabuu("FÜTURSUZ","ÇEKİNMEK","UMURSAMAZ","DAVRANMAK","RAHATLIK","KAYGI");
        Tabuu tabuu560 = new Tabuu("TÜP","OCAK","GAZ","ÇAY","PATLAMAK","MUTFAK");
        Tabuu tabuu561 = new Tabuu("PİKNİK TÜPÜ","YEMEK PİŞİRMEK","ÇAY YAPMAK","KÜÇÜK","GAZ","TAŞIMAK");
        Tabuu tabuu562 = new Tabuu("AYASOFYA","CAMİ","KİLİSE","TURİST","SULTANAHMET","TOPKAPI SARAYI");
        Tabuu tabuu563 = new Tabuu("TUVAL","RESİM","SULU BOYA","KARA KALEM","RESSAM","DA VINCI");
        Tabuu tabuu564 = new Tabuu("YUVALAMA","NOHUT","KÖFTE","GAZİANTEP","ÇORBA","ANALI KIZLI");
        Tabuu tabuu565 = new Tabuu("BOLLYWOOD","SİNEMA","HİNDİSTAN","FİLM","DANS","ESMER");
        Tabuu tabuu566 = new Tabuu("HOLLYWOOD","SİNEMA","AMERİKAN","FİLM","AKSİYON","KAMERA");
        Tabuu tabuu567 = new Tabuu("MİNNET","GÖNÜL","BORÇ","İYİLİK","YARDIM","DUYMAK");
        Tabuu tabuu568 = new Tabuu("BERLİN","ALMANYA","BAŞKENT","DUVAR","FESTİVAL","DÜNYA");
        Tabuu tabuu569 = new Tabuu("ETOBUR","KUŞ","ET","BİTKİ","HAYVAN","CANLI");
        Tabuu tabuu570 = new Tabuu("ÜÇKAĞITÇI","SAHTEKAR","DOLANDIRICI","YALAN","DÜZENBAZ","KANDIRMAK");
        Tabuu tabuu571 = new Tabuu("ZIMBA","BASMAK","ATAÇ","TEL","MAKİNA","ALİMÜNYUM");
        Tabuu tabuu572 = new Tabuu("ZUMBA","DANS","AEROBİK","LATİN","GRUP","SPOR SALONU");
        Tabuu tabuu573 = new Tabuu("ABANMAK","DAYANMAK","TOP","YÜKLENMEK","ÇULLANMAK","AĞIRLIK");
        Tabuu tabuu574 = new Tabuu("TALİP","EVLENMEK","İZDİVAÇ PROGRAMI","ADAY","KIZ","SEVMEK");
        Tabuu tabuu575 = new Tabuu("KULAK MİSAFİRİ","KONUŞMAK","DİNLEMEK","ANLATMAK","DUYMAK","DEDİKODU");
        Tabuu tabuu576 = new Tabuu("DEDİKODU","GÜNAH","KULAK MİSAFİRİ","BAŞKASI","ARKADAN KONUŞMAK","DİN");
        Tabuu tabuu577 = new Tabuu("SEMPOZYUM","SEMİNER","TOPLANTI","KONUŞMAK","BİLGİ","DÜZENLEMEK");
        Tabuu tabuu578 = new Tabuu("AMAÇ","HEDEF","YAPILACAK","HAYAL","EDİNMEK","ÇALIŞMAK");
        Tabuu tabuu579 = new Tabuu("MIZIKA","MÜZİK","ALET","ENSTRÜMAN","DUDAK","ÜFLEMEK");
        Tabuu tabuu580 = new Tabuu("PALDIR KÜLDÜR","GELMEK","DAVETSİZ","MİSAFİR","HIZLI","ANİDEN");
        Tabuu tabuu581 = new Tabuu("ANAÇ","ŞEFKAT","SEVGİ","İLGİ","ÇOCUK","BAKMAK");
        Tabuu tabuu582 = new Tabuu("YÜZLEŞTİRMEK","KARŞI KARŞIYA","YALAN","KONUŞMAK","SIKIŞTIRMAK","TARAF");
        Tabuu tabuu583 = new Tabuu("TAROT","FAL","BAKMAK","GELECEK","GÖRMEK","KAHVE");
        Tabuu tabuu584 = new Tabuu("KAŞMİR","HALI","YÜN","İPEK","ÖRMEK","OTURMAK");
        Tabuu tabuu585 = new Tabuu("KUMAŞ","ETEK","GİYSİ","TERZİ","YÜN","İPEK");
        Tabuu tabuu586 = new Tabuu("SİNDİRİM SİSTEMİ","MİDE","BAĞIRSAK","VÜCUT","YEMEK","ORGAN");
        Tabuu tabuu587 = new Tabuu("KAÇAKÇI","HIRSIZ","KAPKAÇ","UYUŞTURUCU","SATMAK","YASA DIŞI");
        Tabuu tabuu588 = new Tabuu("YUFKA YÜREK","YUMUŞAK","KALP","İYİ","MERHAMET","ÜZÜLMEK");
        Tabuu tabuu589 = new Tabuu("BARFİKS","ÇUBUK","PARALEL","JİMNASTİK","ÇEKMEK","ASILMAK");
        Tabuu tabuu590 = new Tabuu("EZBER BOZMAK","DEĞİŞTİRMEK","YANLIŞ","AKIL","GÖSTERMEK","TUTMAK");
        Tabuu tabuu591 = new Tabuu("SİNERJİ","YARATMAK","ARASINDA","ORTAK","GÜÇ","İSTEK");
        Tabuu tabuu592 = new Tabuu("ŞEYTANA UYMAK","KÖTÜ","HAPİS","SUÇ","ÖFKE","GÜNAH");
        Tabuu tabuu593 = new Tabuu("MÜSVEDDE","KARALAMAK","NOT ALMAK","YAZMAK","KAĞIT","TEMİZ");
        Tabuu tabuu594 = new Tabuu("SALAMURA","ZEYTİN","TUZ","SU","BALIK","YATIRMAK");
        Tabuu tabuu595 = new Tabuu("NAZLANMAK","ŞIMARMAK","NAZLI","İSTEKSİZ","BEKLEMEK","SEVGİLİ");
        Tabuu tabuu596 = new Tabuu("DAMITMAK","DONATMAK","KAYNATMAK","SU","SAF","KİMYA");
        Tabuu tabuu597 = new Tabuu("DAYALI DÖŞELİ","EV","MOBİLYA","EŞYA","KİRALIK","SATILIK");
        Tabuu tabuu598 = new Tabuu("İSOT","KIRMIZI","BİBER","ACI","BAHARAT","URFA");
        Tabuu tabuu599 = new Tabuu("ANEKDOT","OLAY","HİKAYE","ANLATMAK","ÖYKÜ","İLGİNÇ");
        Tabuu tabuu600 = new Tabuu("TABU KIRMAK","ZORLUK","GÜÇ","BELA","BAŞARMAK","HEDEF");
        Tabuu tabuu601 = new Tabuu("DEĞİRMEN","UN","EKMEK","PERVANE","RÜZGAR","TEPE");
        Tabuu tabuu602 = new Tabuu("AFALLAMAK","ŞAŞIRMAK","ANİ","BEKLENMEDİK","SÜRPRİZ","ŞOK");
        Tabuu tabuu603 = new Tabuu("DOĞAÇLAMA","MÜZİK","EZBERLEMEK","ÇALMAK","ÇALIŞMAK","TİYATRO");
        Tabuu tabuu604 = new Tabuu("KÖPÜRTMEK","AYRAN","BALONCUK","KAHVE","SODA","MİKSER");
        Tabuu tabuu605 = new Tabuu("REPLİK","FİLM","SÖYLEMEK","TİYATRO","KONUŞMAK","EZBER");
        Tabuu tabuu606 = new Tabuu("KARAKALEM","ÇİZMEK","SİYAH","BOYA","PİLOT","RESİM");
        Tabuu tabuu607 = new Tabuu("ZİFİRİ KARANLIK","GECE","KARANLIK","IŞIK","GÖRMEK","LAMBA");
        Tabuu tabuu608 = new Tabuu("ÇİZMEYİ AŞMAK","İLERİ GİTMEK","AŞMAK","SINIR","BİLMEK","ÇİZGİ");
        Tabuu tabuu609 = new Tabuu("BOŞBOĞAZ","KONUŞMAK","GEVEZE","SIR","SÖYLEMEK","ANLATMAK");
        Tabuu tabuu610 = new Tabuu("İADEİZİYARET","GÖRÜŞMEK","GERİ","GELMEK","GİTMEK","EV");





        tabuuList.add(tabuu001);
        tabuuList.add(tabuu002);
        tabuuList.add(tabuu003);
        tabuuList.add(tabuu004);
        tabuuList.add(tabuu005);
        tabuuList.add(tabuu006);
        tabuuList.add(tabuu007);
        tabuuList.add(tabuu008);
        tabuuList.add(tabuu009);
        tabuuList.add(tabuu010);
        tabuuList.add(tabuu011);
        tabuuList.add(tabuu012);
        tabuuList.add(tabuu013);
        tabuuList.add(tabuu014);
        tabuuList.add(tabuu015);
        tabuuList.add(tabuu016);
        tabuuList.add(tabuu017);
        tabuuList.add(tabuu018);
        tabuuList.add(tabuu019);
        tabuuList.add(tabuu020);
        tabuuList.add(tabuu021);
        tabuuList.add(tabuu022);
        tabuuList.add(tabuu023);
        tabuuList.add(tabuu024);
        tabuuList.add(tabuu025);
        tabuuList.add(tabuu026);
        tabuuList.add(tabuu027);
        tabuuList.add(tabuu028);
        tabuuList.add(tabuu029);
        tabuuList.add(tabuu030);
        tabuuList.add(tabuu031);
        tabuuList.add(tabuu032);
        tabuuList.add(tabuu033);
        tabuuList.add(tabuu034);
        tabuuList.add(tabuu035);
        tabuuList.add(tabuu036);
        tabuuList.add(tabuu037);
        tabuuList.add(tabuu038);
        tabuuList.add(tabuu039);
        tabuuList.add(tabuu040);
        tabuuList.add(tabuu041);
        tabuuList.add(tabuu042);
        tabuuList.add(tabuu043);
        tabuuList.add(tabuu044);
        tabuuList.add(tabuu045);
        tabuuList.add(tabuu046);
        tabuuList.add(tabuu047);
        tabuuList.add(tabuu048);
        tabuuList.add(tabuu049);
        tabuuList.add(tabuu050);
        tabuuList.add(tabuu051);
        tabuuList.add(tabuu052);
        tabuuList.add(tabuu053);
        tabuuList.add(tabuu054);
        tabuuList.add(tabuu055);
        tabuuList.add(tabuu056);
        tabuuList.add(tabuu057);
        tabuuList.add(tabuu058);
        tabuuList.add(tabuu059);
        tabuuList.add(tabuu060);
        tabuuList.add(tabuu061);
        tabuuList.add(tabuu062);
        tabuuList.add(tabuu063);
        tabuuList.add(tabuu064);
        tabuuList.add(tabuu065);

        SharedPreferences sharedPreferencesApprove = getSharedPreferences("PlayStoreComment",MODE_PRIVATE);
        int commentstatus = sharedPreferencesApprove.getInt("icommented",0);

        if (commentstatus == 1){
            tabuuList.add(tabuu066);
            tabuuList.add(tabuu067);
            tabuuList.add(tabuu068);
            tabuuList.add(tabuu069);
            tabuuList.add(tabuu070);
            tabuuList.add(tabuu071);
            tabuuList.add(tabuu072);
            tabuuList.add(tabuu073);
            tabuuList.add(tabuu074);
            tabuuList.add(tabuu075);
            tabuuList.add(tabuu076);
            tabuuList.add(tabuu077);
            tabuuList.add(tabuu078);
            tabuuList.add(tabuu079);
            tabuuList.add(tabuu080);
            tabuuList.add(tabuu081);
            tabuuList.add(tabuu082);
            tabuuList.add(tabuu083);
            tabuuList.add(tabuu084);
            tabuuList.add(tabuu085);
            tabuuList.add(tabuu086);
            tabuuList.add(tabuu087);
            tabuuList.add(tabuu088);
            tabuuList.add(tabuu089);
            tabuuList.add(tabuu090);
            tabuuList.add(tabuu091);
            tabuuList.add(tabuu092);
            tabuuList.add(tabuu093);
            tabuuList.add(tabuu094);
            tabuuList.add(tabuu095);
            tabuuList.add(tabuu096);
            tabuuList.add(tabuu097);
            tabuuList.add(tabuu098);
            tabuuList.add(tabuu099);
        }


        tabuuList.add(tabuu);
        tabuuList.add(tabuu1);
        tabuuList.add(tabuu2);
        tabuuList.add(tabuu3);
        tabuuList.add(tabuu4);
        tabuuList.add(tabuu5);
        tabuuList.add(tabuu6);
        tabuuList.add(tabuu7);
        tabuuList.add(tabuu8);
        tabuuList.add(tabuu9);
        tabuuList.add(tabuu10);
        tabuuList.add(tabuu11);
        tabuuList.add(tabuu12);
        tabuuList.add(tabuu13);
        tabuuList.add(tabuu14);
        tabuuList.add(tabuu15);
        tabuuList.add(tabuu16);
        tabuuList.add(tabuu17);
        tabuuList.add(tabuu18);
        tabuuList.add(tabuu19);
        tabuuList.add(tabuu20);
        tabuuList.add(tabuu21);
        tabuuList.add(tabuu22);
        tabuuList.add(tabuu23);
        tabuuList.add(tabuu24);
        tabuuList.add(tabuu25);
        tabuuList.add(tabuu26);
        tabuuList.add(tabuu27);
        tabuuList.add(tabuu28);
        tabuuList.add(tabuu29);
        tabuuList.add(tabuu30);


        tabuuList.add(tabuu31);
        tabuuList.add(tabuu32);
        tabuuList.add(tabuu33);
        tabuuList.add(tabuu34);
        tabuuList.add(tabuu35);
        tabuuList.add(tabuu36);
        tabuuList.add(tabuu37);
        tabuuList.add(tabuu38);
        tabuuList.add(tabuu39);
        tabuuList.add(tabuu40);
        tabuuList.add(tabuu41);
        tabuuList.add(tabuu42);
        tabuuList.add(tabuu43);
        tabuuList.add(tabuu44);
        tabuuList.add(tabuu45);
        tabuuList.add(tabuu46);
        tabuuList.add(tabuu47);
        tabuuList.add(tabuu48);
        tabuuList.add(tabuu49);
        tabuuList.add(tabuu50);
        tabuuList.add(tabuu51);


        tabuuList.add(tabuu52);
        tabuuList.add(tabuu53);
        tabuuList.add(tabuu54);
        tabuuList.add(tabuu55);
        tabuuList.add(tabuu56);
        tabuuList.add(tabuu57);
        tabuuList.add(tabuu58);
        tabuuList.add(tabuu59);
        tabuuList.add(tabuu60);
        tabuuList.add(tabuu61);
        tabuuList.add(tabuu62);
        tabuuList.add(tabuu63);
        tabuuList.add(tabuu64);
        tabuuList.add(tabuu65);
        tabuuList.add(tabuu66);
        tabuuList.add(tabuu67);
        tabuuList.add(tabuu68);
        tabuuList.add(tabuu69);
        tabuuList.add(tabuu70);
        tabuuList.add(tabuu71);
        tabuuList.add(tabuu72);
        tabuuList.add(tabuu73);
        tabuuList.add(tabuu74);
        tabuuList.add(tabuu75);
        tabuuList.add(tabuu76);
        tabuuList.add(tabuu77);
        tabuuList.add(tabuu78);


        tabuuList.add(tabuu79);
        tabuuList.add(tabuu80);
        tabuuList.add(tabuu81);
        tabuuList.add(tabuu82);
        tabuuList.add(tabuu83);
        tabuuList.add(tabuu84);
        tabuuList.add(tabuu85);
        tabuuList.add(tabuu86);
        tabuuList.add(tabuu87);
        tabuuList.add(tabuu88);
        tabuuList.add(tabuu89);
        tabuuList.add(tabuu90);
        tabuuList.add(tabuu91);
        tabuuList.add(tabuu92);
        tabuuList.add(tabuu93);
        tabuuList.add(tabuu94);
        tabuuList.add(tabuu95);
        tabuuList.add(tabuu96);
        tabuuList.add(tabuu97);
        tabuuList.add(tabuu98);
        tabuuList.add(tabuu99);
        tabuuList.add(tabuu100);


        tabuuList.add(tabuu101);
        tabuuList.add(tabuu102);
        tabuuList.add(tabuu103);
        tabuuList.add(tabuu104);
        tabuuList.add(tabuu105);
        tabuuList.add(tabuu106);
        tabuuList.add(tabuu107);
        tabuuList.add(tabuu108);
        tabuuList.add(tabuu109);
        tabuuList.add(tabuu110);
        tabuuList.add(tabuu111);
        tabuuList.add(tabuu112);
        tabuuList.add(tabuu113);
        tabuuList.add(tabuu114);
        tabuuList.add(tabuu115);
        tabuuList.add(tabuu116);
        tabuuList.add(tabuu117);
        tabuuList.add(tabuu118);
        tabuuList.add(tabuu119);
        tabuuList.add(tabuu120);
        tabuuList.add(tabuu121);
        tabuuList.add(tabuu122);
        tabuuList.add(tabuu123);
        tabuuList.add(tabuu124);
        tabuuList.add(tabuu125);
        tabuuList.add(tabuu126);
        tabuuList.add(tabuu127);
        tabuuList.add(tabuu128);
        tabuuList.add(tabuu129);
        tabuuList.add(tabuu130);
        tabuuList.add(tabuu131);
        tabuuList.add(tabuu132);
        tabuuList.add(tabuu133);
        tabuuList.add(tabuu134);
        tabuuList.add(tabuu135);
        tabuuList.add(tabuu136);
        tabuuList.add(tabuu137);
        tabuuList.add(tabuu138);
        tabuuList.add(tabuu139);
        tabuuList.add(tabuu140);
        tabuuList.add(tabuu141);
        tabuuList.add(tabuu142);
        tabuuList.add(tabuu143);
        tabuuList.add(tabuu144);
        tabuuList.add(tabuu145);
        tabuuList.add(tabuu146);
        tabuuList.add(tabuu147);
        tabuuList.add(tabuu148);
        tabuuList.add(tabuu149);
        tabuuList.add(tabuu150);
        tabuuList.add(tabuu151);


        tabuuList.add(tabuu152);
        tabuuList.add(tabuu153);
        tabuuList.add(tabuu154);
        tabuuList.add(tabuu155);
        tabuuList.add(tabuu156);
        tabuuList.add(tabuu157);
        tabuuList.add(tabuu158);
        tabuuList.add(tabuu159);
        tabuuList.add(tabuu160);
        tabuuList.add(tabuu161);
        tabuuList.add(tabuu162);
        tabuuList.add(tabuu163);
        tabuuList.add(tabuu164);
        tabuuList.add(tabuu165);
        tabuuList.add(tabuu166);
        tabuuList.add(tabuu167);
        tabuuList.add(tabuu168);
        tabuuList.add(tabuu169);
        tabuuList.add(tabuu170);
        tabuuList.add(tabuu171);
        tabuuList.add(tabuu172);
        tabuuList.add(tabuu173);
        tabuuList.add(tabuu174);
        tabuuList.add(tabuu175);
        tabuuList.add(tabuu176);
        tabuuList.add(tabuu177);
        tabuuList.add(tabuu178);
        tabuuList.add(tabuu179);
        tabuuList.add(tabuu180);
        tabuuList.add(tabuu181);
        tabuuList.add(tabuu182);
        tabuuList.add(tabuu183);
        tabuuList.add(tabuu184);
        tabuuList.add(tabuu185);
        tabuuList.add(tabuu186);
        tabuuList.add(tabuu187);
        tabuuList.add(tabuu188);
        tabuuList.add(tabuu189);
        tabuuList.add(tabuu190);
        tabuuList.add(tabuu191);
        tabuuList.add(tabuu192);
        tabuuList.add(tabuu193);
        tabuuList.add(tabuu194);
        tabuuList.add(tabuu195);
        tabuuList.add(tabuu196);
        tabuuList.add(tabuu197);
        tabuuList.add(tabuu198);
        tabuuList.add(tabuu199);
        tabuuList.add(tabuu200);
        tabuuList.add(tabuu201);
        tabuuList.add(tabuu202);
        tabuuList.add(tabuu203);
        tabuuList.add(tabuu204);
        tabuuList.add(tabuu205);
        tabuuList.add(tabuu206);
        tabuuList.add(tabuu207);
        tabuuList.add(tabuu208);
        tabuuList.add(tabuu209);
        tabuuList.add(tabuu210);
        tabuuList.add(tabuu211);
        tabuuList.add(tabuu212);
        tabuuList.add(tabuu213);
        tabuuList.add(tabuu214);
        tabuuList.add(tabuu215);
        tabuuList.add(tabuu216);
        tabuuList.add(tabuu217);
        tabuuList.add(tabuu218);
        tabuuList.add(tabuu219);
        tabuuList.add(tabuu220);
        tabuuList.add(tabuu221);
        tabuuList.add(tabuu222);
        tabuuList.add(tabuu223);
        tabuuList.add(tabuu224);
        tabuuList.add(tabuu225);
        tabuuList.add(tabuu226);
        tabuuList.add(tabuu227);
        tabuuList.add(tabuu228);
        tabuuList.add(tabuu229);
        tabuuList.add(tabuu230);
        tabuuList.add(tabuu231);
        tabuuList.add(tabuu232);
        tabuuList.add(tabuu233);
        tabuuList.add(tabuu234);
        tabuuList.add(tabuu235);
        tabuuList.add(tabuu236);
        tabuuList.add(tabuu237);
        tabuuList.add(tabuu238);
        tabuuList.add(tabuu239);
        tabuuList.add(tabuu240);
        tabuuList.add(tabuu241);
        tabuuList.add(tabuu242);
        tabuuList.add(tabuu243);
        tabuuList.add(tabuu244);
        tabuuList.add(tabuu245);
        tabuuList.add(tabuu246);
        tabuuList.add(tabuu247);
        tabuuList.add(tabuu248);
        tabuuList.add(tabuu249);
        tabuuList.add(tabuu250);







        //MAĞAZA'DA SATIN ALIMLAR İÇİN KULLANILACAK

        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
        boolean buyprocess = sharedPreferences.getBoolean("buy150classictabuu",false);

        if (buyprocess){
            tabuuList.add(tabuu251);
            tabuuList.add(tabuu252);
            tabuuList.add(tabuu253);
            tabuuList.add(tabuu254);
            tabuuList.add(tabuu255);
            tabuuList.add(tabuu256);
            tabuuList.add(tabuu257);
            tabuuList.add(tabuu258);
            tabuuList.add(tabuu259);
            tabuuList.add(tabuu260);
            tabuuList.add(tabuu261);
            tabuuList.add(tabuu262);
            tabuuList.add(tabuu263);
            tabuuList.add(tabuu264);
            tabuuList.add(tabuu265);
            tabuuList.add(tabuu266);
            tabuuList.add(tabuu267);
            tabuuList.add(tabuu268);
            tabuuList.add(tabuu269);
            tabuuList.add(tabuu270);
            tabuuList.add(tabuu271);
            tabuuList.add(tabuu272);
            tabuuList.add(tabuu273);
            tabuuList.add(tabuu274);
            tabuuList.add(tabuu275);
            tabuuList.add(tabuu276);
            tabuuList.add(tabuu277);
            tabuuList.add(tabuu278);
            tabuuList.add(tabuu279);
            tabuuList.add(tabuu280);
            tabuuList.add(tabuu281);
            tabuuList.add(tabuu282);
            tabuuList.add(tabuu283);
            tabuuList.add(tabuu284);
            tabuuList.add(tabuu285);
            tabuuList.add(tabuu286);
            tabuuList.add(tabuu287);
            tabuuList.add(tabuu288);
            tabuuList.add(tabuu289);
            tabuuList.add(tabuu290);
            tabuuList.add(tabuu291);
            tabuuList.add(tabuu292);
            tabuuList.add(tabuu293);
            tabuuList.add(tabuu294);
            tabuuList.add(tabuu295);
            tabuuList.add(tabuu296);
            tabuuList.add(tabuu297);
            tabuuList.add(tabuu298);
            tabuuList.add(tabuu299);
            tabuuList.add(tabuu300);
            tabuuList.add(tabuu301);
            tabuuList.add(tabuu302);
            tabuuList.add(tabuu303);
            tabuuList.add(tabuu304);
            tabuuList.add(tabuu305);
            tabuuList.add(tabuu306);
            tabuuList.add(tabuu307);
            tabuuList.add(tabuu308);
            tabuuList.add(tabuu309);
            tabuuList.add(tabuu310);
            tabuuList.add(tabuu311);
            tabuuList.add(tabuu312);
            tabuuList.add(tabuu313);
            tabuuList.add(tabuu314);
            tabuuList.add(tabuu315);
            tabuuList.add(tabuu316);
            tabuuList.add(tabuu317);
            tabuuList.add(tabuu318);
            tabuuList.add(tabuu319);
            tabuuList.add(tabuu320);
            tabuuList.add(tabuu321);
            tabuuList.add(tabuu322);
            tabuuList.add(tabuu323);
            tabuuList.add(tabuu324);
            tabuuList.add(tabuu325);
            tabuuList.add(tabuu326);
            tabuuList.add(tabuu327);
            tabuuList.add(tabuu328);
            tabuuList.add(tabuu329);
            tabuuList.add(tabuu330);
            tabuuList.add(tabuu331);
            tabuuList.add(tabuu332);
            tabuuList.add(tabuu333);
            tabuuList.add(tabuu334);
            tabuuList.add(tabuu335);
            tabuuList.add(tabuu336);
            tabuuList.add(tabuu337);
            tabuuList.add(tabuu338);
            tabuuList.add(tabuu339);
            tabuuList.add(tabuu340);
            tabuuList.add(tabuu341);
            tabuuList.add(tabuu342);
            tabuuList.add(tabuu343);
            tabuuList.add(tabuu344);
            tabuuList.add(tabuu345);
            tabuuList.add(tabuu346);
            tabuuList.add(tabuu347);
            tabuuList.add(tabuu348);
            tabuuList.add(tabuu349);
            tabuuList.add(tabuu350);
            tabuuList.add(tabuu351);
            tabuuList.add(tabuu352);
            tabuuList.add(tabuu353);
            tabuuList.add(tabuu354);
            tabuuList.add(tabuu355);
            tabuuList.add(tabuu356);
            tabuuList.add(tabuu357);
            tabuuList.add(tabuu358);
            tabuuList.add(tabuu359);
            tabuuList.add(tabuu360);
            tabuuList.add(tabuu361);
            tabuuList.add(tabuu362);
            tabuuList.add(tabuu363);
            tabuuList.add(tabuu364);
            tabuuList.add(tabuu365);
            tabuuList.add(tabuu366);
            tabuuList.add(tabuu367);
            tabuuList.add(tabuu368);
            tabuuList.add(tabuu369);
            tabuuList.add(tabuu370);
            tabuuList.add(tabuu371);
            tabuuList.add(tabuu372);
            tabuuList.add(tabuu373);
            tabuuList.add(tabuu374);
            tabuuList.add(tabuu375);
            tabuuList.add(tabuu376);
            tabuuList.add(tabuu377);
            tabuuList.add(tabuu378);
            tabuuList.add(tabuu379);
            tabuuList.add(tabuu380);
            tabuuList.add(tabuu381);
            tabuuList.add(tabuu382);
            tabuuList.add(tabuu383);
            tabuuList.add(tabuu384);
            tabuuList.add(tabuu385);
            tabuuList.add(tabuu386);
            tabuuList.add(tabuu387);
            tabuuList.add(tabuu388);
            tabuuList.add(tabuu389);
            tabuuList.add(tabuu390);
            tabuuList.add(tabuu391);
            tabuuList.add(tabuu392);
            tabuuList.add(tabuu393);
            tabuuList.add(tabuu394);
            tabuuList.add(tabuu395);
            tabuuList.add(tabuu396);
            tabuuList.add(tabuu397);
            tabuuList.add(tabuu398);
            tabuuList.add(tabuu399);
            tabuuList.add(tabuu400);


            //Mağaza için
            sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
            boolean buyprocess2 = sharedPreferences.getBoolean("buy300classictabuu",false);

            if (buyprocess2){
                tabuuList.add(tabuu401);
                tabuuList.add(tabuu402);
                tabuuList.add(tabuu403);
                tabuuList.add(tabuu404);
                tabuuList.add(tabuu405);
                tabuuList.add(tabuu406);
                tabuuList.add(tabuu407);
                tabuuList.add(tabuu408);
                tabuuList.add(tabuu409);
                tabuuList.add(tabuu410);
                tabuuList.add(tabuu411);
                tabuuList.add(tabuu412);
                tabuuList.add(tabuu413);
                tabuuList.add(tabuu414);
                tabuuList.add(tabuu415);
                tabuuList.add(tabuu416);
                tabuuList.add(tabuu417);
                tabuuList.add(tabuu418);
                tabuuList.add(tabuu419);
                tabuuList.add(tabuu420);
                tabuuList.add(tabuu421);
                tabuuList.add(tabuu422);
                tabuuList.add(tabuu423);
                tabuuList.add(tabuu424);
                tabuuList.add(tabuu425);
                tabuuList.add(tabuu426);
                tabuuList.add(tabuu427);
                tabuuList.add(tabuu428);
                tabuuList.add(tabuu429);
                tabuuList.add(tabuu430);
                tabuuList.add(tabuu431);
                tabuuList.add(tabuu432);
                tabuuList.add(tabuu433);
                tabuuList.add(tabuu434);
                tabuuList.add(tabuu435);
                tabuuList.add(tabuu436);
                tabuuList.add(tabuu437);
                tabuuList.add(tabuu438);
                tabuuList.add(tabuu439);
                tabuuList.add(tabuu440);
                tabuuList.add(tabuu441);
                tabuuList.add(tabuu442);
                tabuuList.add(tabuu443);
                tabuuList.add(tabuu444);
                tabuuList.add(tabuu445);
                tabuuList.add(tabuu446);
                tabuuList.add(tabuu447);
                tabuuList.add(tabuu448);
                tabuuList.add(tabuu449);
                tabuuList.add(tabuu450);
                tabuuList.add(tabuu451);
                tabuuList.add(tabuu452);
                tabuuList.add(tabuu453);
                tabuuList.add(tabuu454);
                tabuuList.add(tabuu455);
                tabuuList.add(tabuu456);
                tabuuList.add(tabuu457);
                tabuuList.add(tabuu458);
                tabuuList.add(tabuu459);
                tabuuList.add(tabuu460);
                tabuuList.add(tabuu461);
                tabuuList.add(tabuu462);
                tabuuList.add(tabuu463);
                tabuuList.add(tabuu464);
                tabuuList.add(tabuu465);
                tabuuList.add(tabuu466);
                tabuuList.add(tabuu467);
                tabuuList.add(tabuu468);
                tabuuList.add(tabuu469);
                tabuuList.add(tabuu470);
                tabuuList.add(tabuu471);
                tabuuList.add(tabuu472);
                tabuuList.add(tabuu473);
                tabuuList.add(tabuu474);
                tabuuList.add(tabuu475);
                tabuuList.add(tabuu476);
                tabuuList.add(tabuu477);
                tabuuList.add(tabuu478);
                tabuuList.add(tabuu479);
                tabuuList.add(tabuu480);
                tabuuList.add(tabuu481);
                tabuuList.add(tabuu482);
                tabuuList.add(tabuu483);
                tabuuList.add(tabuu484);
                tabuuList.add(tabuu485);
                tabuuList.add(tabuu486);
                tabuuList.add(tabuu487);
                tabuuList.add(tabuu488);
                tabuuList.add(tabuu489);
                tabuuList.add(tabuu490);
                tabuuList.add(tabuu491);
                tabuuList.add(tabuu492);
                tabuuList.add(tabuu493);
                tabuuList.add(tabuu494);
                tabuuList.add(tabuu495);
                tabuuList.add(tabuu496);
                tabuuList.add(tabuu497);
                tabuuList.add(tabuu498);
                tabuuList.add(tabuu499);
                tabuuList.add(tabuu500);
                tabuuList.add(tabuu501);
                tabuuList.add(tabuu502);
                tabuuList.add(tabuu503);
                tabuuList.add(tabuu504);
                tabuuList.add(tabuu505);
                tabuuList.add(tabuu506);
                tabuuList.add(tabuu507);
                tabuuList.add(tabuu508);
                tabuuList.add(tabuu509);
                tabuuList.add(tabuu510);
                tabuuList.add(tabuu511);
                tabuuList.add(tabuu512);
                tabuuList.add(tabuu513);
                tabuuList.add(tabuu514);
                tabuuList.add(tabuu515);
                tabuuList.add(tabuu516);
                tabuuList.add(tabuu517);
                tabuuList.add(tabuu518);
                tabuuList.add(tabuu519);
                tabuuList.add(tabuu520);
                tabuuList.add(tabuu521);
                tabuuList.add(tabuu522);
                tabuuList.add(tabuu523);
                tabuuList.add(tabuu524);
                tabuuList.add(tabuu525);
                tabuuList.add(tabuu526);
                tabuuList.add(tabuu527);
                tabuuList.add(tabuu528);
                tabuuList.add(tabuu529);
                tabuuList.add(tabuu530);
                tabuuList.add(tabuu531);
                tabuuList.add(tabuu532);
                tabuuList.add(tabuu533);
                tabuuList.add(tabuu534);
                tabuuList.add(tabuu535);
                tabuuList.add(tabuu536);
                tabuuList.add(tabuu537);
                tabuuList.add(tabuu538);
                tabuuList.add(tabuu539);
                tabuuList.add(tabuu540);
                tabuuList.add(tabuu541);
                tabuuList.add(tabuu542);
                tabuuList.add(tabuu543);
                tabuuList.add(tabuu544);
                tabuuList.add(tabuu545);
                tabuuList.add(tabuu546);
                tabuuList.add(tabuu547);
                tabuuList.add(tabuu548);
                tabuuList.add(tabuu549);
                tabuuList.add(tabuu550);
                tabuuList.add(tabuu551);
                tabuuList.add(tabuu552);
                tabuuList.add(tabuu553);
                tabuuList.add(tabuu554);
                tabuuList.add(tabuu555);
                tabuuList.add(tabuu556);
                tabuuList.add(tabuu557);
                tabuuList.add(tabuu558);
                tabuuList.add(tabuu559);
                tabuuList.add(tabuu560);
                tabuuList.add(tabuu561);
                tabuuList.add(tabuu562);
                tabuuList.add(tabuu563);
                tabuuList.add(tabuu564);
                tabuuList.add(tabuu565);
                tabuuList.add(tabuu566);
                tabuuList.add(tabuu567);
                tabuuList.add(tabuu568);
                tabuuList.add(tabuu569);
                tabuuList.add(tabuu570);
                tabuuList.add(tabuu571);
                tabuuList.add(tabuu572);
                tabuuList.add(tabuu573);
                tabuuList.add(tabuu574);
                tabuuList.add(tabuu575);
                tabuuList.add(tabuu576);
                tabuuList.add(tabuu577);
                tabuuList.add(tabuu578);
                tabuuList.add(tabuu579);
                tabuuList.add(tabuu580);
                tabuuList.add(tabuu581);
                tabuuList.add(tabuu582);
                tabuuList.add(tabuu583);
                tabuuList.add(tabuu584);
                tabuuList.add(tabuu585);
                tabuuList.add(tabuu586);
                tabuuList.add(tabuu587);
                tabuuList.add(tabuu588);
                tabuuList.add(tabuu589);
                tabuuList.add(tabuu590);
                tabuuList.add(tabuu591);
                tabuuList.add(tabuu592);
                tabuuList.add(tabuu593);
                tabuuList.add(tabuu594);
                tabuuList.add(tabuu595);
                tabuuList.add(tabuu596);
                tabuuList.add(tabuu597);
                tabuuList.add(tabuu598);
                tabuuList.add(tabuu599);
                tabuuList.add(tabuu600);
                tabuuList.add(tabuu601);
                tabuuList.add(tabuu602);
                tabuuList.add(tabuu603);
                tabuuList.add(tabuu604);
                tabuuList.add(tabuu605);
                tabuuList.add(tabuu606);
                tabuuList.add(tabuu607);
                tabuuList.add(tabuu608);
                tabuuList.add(tabuu609);
                tabuuList.add(tabuu610);
            }




        }

        //shared preferences kontrolünü burada yap eğer 1 gelirse yani kullanıcı paketi satın aldıysa if de true sağlanmış olacak ve tabu kartları kullanıcının hesabına yüklenmiş olacak.

    }

    private void getRandomList(List<Tabuu> tabuuList) {

        int index = random.nextInt(tabuuList.size());
        textViewPlayTabooWord.setText(tabuuList.get(index).getTabuu());
        textViewPlayTaboo1.setText(tabuuList.get(index).getTabuua());
        textViewPlayTaboo2.setText(tabuuList.get(index).getTabuub());
        textViewPlayTaboo3.setText(tabuuList.get(index).getTabuuc());
        textViewPlayTaboo4.setText(tabuuList.get(index).getTabuud());
        textViewPlayTaboo5.setText(tabuuList.get(index).getTabuue());

        tabuuList.remove(index);


    }





    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassicPlayActivity.this);
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


                Intent intent = new Intent(ClassicPlayActivity.this,MainActivity.class);
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

                if (purchase.getSku().equals("300classictabuu")){

                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (firebaseUser != null){
                        String uid = firebaseUser.getUid();

                        firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference("purchases300").child(firebaseUser.getUid());
                        com.furkanayaz.anlatbakalimcizbakalim.Purchase purchase300 = new com.furkanayaz.anlatbakalimcizbakalim.Purchase(uid);
                        databaseReference.push().setValue(purchase300);


                        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("buy300classictabuu",true);
                        editor.commit();
                    }else {
                        Toast.makeText(ClassicPlayActivity.this,"Lütfen satın alma işleminizi oyuna üye olarak gerçekleştiriniz",Toast.LENGTH_LONG).show();
                    }


                }

            }
        }

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassicPlayActivity.this);
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
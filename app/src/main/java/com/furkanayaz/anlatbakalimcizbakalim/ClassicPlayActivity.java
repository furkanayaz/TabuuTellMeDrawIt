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
                    Toast.makeText(ClassicPlayActivity.this,"??deme sistemi i??in Google Play hesab??n??z?? kontrol ediniz",Toast.LENGTH_LONG).show();
                    //cardViewStatusChanger(false);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(ClassicPlayActivity.this,"??deme i??lemi sa??lanamad??",Toast.LENGTH_LONG).show();
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


        passCounterA = pass; // passcounter ile azaltma i??lemi yap??yorum.
        //tabuuCounter = tabuu; // tabuuCounter'?? hala kullan??yorum ve butona ka?? defa t??klan??ld??????n?? ????reniyorum. tabuu ise correctNumber'dan ????karma i??lemini yap??yor.
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
                    Snackbar.make(v,"Maalesef tabu kart??n??z bitti. Tabu kart?? sat??n almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (firebaseUser != null){

                                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuINAPPDetailList.get(1)).build();

                                        billingClient.launchBillingFlow(ClassicPlayActivity.this,flowParams);






                                        Snackbar.make(v,"Tabuu kartlar??n??z ba??ar??l?? bir ??ekilde sat??n al??nd?? :)",Snackbar.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(ClassicPlayActivity.this,"Sat??n al??mlar??n??z?? uygulamaya ??ye olarak ger??ekle??tiriniz",Toast.LENGTH_LONG).show();
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
                    Snackbar.make(v,"Maalesef tabu kart??n??z bitti. Tabu kart?? sat??n almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(v,"Tabuu kartlar??n??z ba??ar??l?? bir ??ekilde sat??n al??nd?? :)",Snackbar.LENGTH_LONG).show();
                                }
                            }).show();
                }else {
                    if (textViewPlayClassicTeamA.getText().toString().equals(teamsList.get(0).getTeamAname())){
                        if (passCounterA<=0){
                            Toast.makeText(ClassicPlayActivity.this,"Pas hakk??n??z bitti",Toast.LENGTH_SHORT).show();
                        }else {
                            passCounterA--;
                            Toast.makeText(ClassicPlayActivity.this,passCounterA+" pas hakk??n??z kald??",Toast.LENGTH_SHORT).show();
                            getRandomList(tabuuList);
                        }
                    }else {
                        if (passCounterB<=0){
                            Toast.makeText(ClassicPlayActivity.this,"Pas hakk??n??z bitti",Toast.LENGTH_SHORT).show();
                        }else {
                            passCounterB--;
                            Toast.makeText(ClassicPlayActivity.this,passCounterB+" pas hakk??n??z kald??",Toast.LENGTH_SHORT).show();
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
                    Snackbar.make(v,"Maalesef tabu kart??n??z bitti. Tabu kart?? sat??n almak istiyor musunuz?",Snackbar.LENGTH_LONG)
                            .setAction("SATIN AL", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(v,"Tabuu kartlar??n??z ba??ar??l?? bir ??ekilde sat??n al??nd?? :)",Snackbar.LENGTH_LONG).show();
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
            builder.setPositiveButton("YEN?? OYUN OYNA", new DialogInterface.OnClickListener() {
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

        Tabuu tabuu001 = new Tabuu("KAZIK","A??A??","ODUN","????V??","DEM??R","OTURMAK");
        Tabuu tabuu002 = new Tabuu("K??TEK","DAYAK","SOPA","YEMEK","??FKE","B??Y??K");
        Tabuu tabuu003 = new Tabuu("AN??ME","KORE","JAPON","KAR??KAT??R","MANGA","NARUTO");
        Tabuu tabuu004 = new Tabuu("DUDAK OKUMAK","SESS??Z","HABER","ENGELL??","A??IZ","D??L");
        Tabuu tabuu005 = new Tabuu("HEZARFEN","POL??MAT","B??LG??","B??L??M","??SLAM","AHMET ??ELEB??");
        Tabuu tabuu006 = new Tabuu("TOLSTOY","RUSYA","YAZAR","SOVYET","EDEB??YAT","ANNA KARENNINA");
        Tabuu tabuu007 = new Tabuu("MUASIR","??A??DA??","GEL????M????","ATAT??RK","MEDEN??YET","SEV??YE");
        Tabuu tabuu008 = new Tabuu("KU??BURNU","??AY","TOZ","KIRMIZI","TURUNCU","TATLI");
        Tabuu tabuu009 = new Tabuu("ERC??YES","KAR","DA??","Y??KSEK","SO??UK","KAYSER??");
        Tabuu tabuu010 = new Tabuu("NARG??LE","T??T??N","S??GARA","ZEH??R","????MEK","KAFE");
        Tabuu tabuu011 = new Tabuu("BUGATTI VEYRON","LAMBORGHINI","YARI??","ARABA","HIZ","PAHALI");
        Tabuu tabuu012 = new Tabuu("TABURCU OLMAK","??ST??RAHAT","B??TMEK","HASTALIK","HASTAHANE","ECZANE");
        Tabuu tabuu013 = new Tabuu("??NT??KAM","DAVA","SONRADAN","????","TEPK??","KAR??ILIK");
        Tabuu tabuu014 = new Tabuu("??LSER","KANSER","M??DE","HASTALIK","DOKTOR","A??RI");
        Tabuu tabuu015 = new Tabuu("??RG??","??P","Y??N","TI??","BABANNE","??NL??K");
        Tabuu tabuu016 = new Tabuu("HAYVAR","BALIK","PAHALI","YEMEK","??AMPANYA","YUMURTA");
        Tabuu tabuu017 = new Tabuu("HATIRLAMAK","AKLINA GELMEK","SONRADAN","GE??M????","ESK??","VAR");
        Tabuu tabuu018 = new Tabuu("KORSE","SIRT","BOYUN","D??ZELTMEK","RAHATLIK","E??R??");
        Tabuu tabuu019 = new Tabuu("BOWLING","TOP","DEV??RMEK","OYUN","A??IR","DEL??K");
        Tabuu tabuu020 = new Tabuu("S??SPANS??YON","YAY","ZIPLAMAK","ARABA","B??S??KLET","DA??");
        Tabuu tabuu021 = new Tabuu("AK??M??LAT??R","ELEKTR??K","DEPO","??ARJ","KUTUP","ARABA");
        Tabuu tabuu022 = new Tabuu("KULAKLIK","TAKMAK","M??Z??K","D??NLEMEK","VIDEO","FILM");
        Tabuu tabuu023 = new Tabuu("KUMP??R","PATATES","MISIR","SICAK","SOBA","FIRIN");
        Tabuu tabuu024 = new Tabuu("??SKENDER","TAVUK","ET","SOS","YO??URT","LOKANTA");
        Tabuu tabuu025 = new Tabuu("??????K??FTE","T??RK","ACI SOS","NAR EK????S??","AYRAN","D??R??M");
        Tabuu tabuu026 = new Tabuu("JAVA","KOTLIN","DART","FLUTTER","PROGRAMLAMA","MOB??L");
        Tabuu tabuu027 = new Tabuu("EC???? B??C????","D??ZG??N","E??R??","??ARPIK","BOZUK","????RK??N");
        Tabuu tabuu028 = new Tabuu("TIPKI","BENZEMEK","AYNI","K??????","Y??Z","??FADE");
        Tabuu tabuu029 = new Tabuu("KIPIRTI","KALP","DUYGU","D??????NCE","HAREKET","OLAY");
        Tabuu tabuu030 = new Tabuu("CADILAR BAYRAMI","KORKUN??","NOEL","HRISTIYAN","AVRUPA","KOST??M");
        Tabuu tabuu031 = new Tabuu("YELLENMEK","??SHAL","TUVALET","ETMEK","??HT??YA??","HASTA");
        Tabuu tabuu032 = new Tabuu("EMEK????","??ALI??MAK","SERMAYE","PARA","VESA??","YA??LI");
        Tabuu tabuu033 = new Tabuu("C????ER","ORGAN","HAYVAN","KURBAN","????","YEMEK");
        Tabuu tabuu034 = new Tabuu("KEFEN","MEZAR","MORG","HASTAHANE","??L??M","CENAZE");
        Tabuu tabuu035 = new Tabuu("DEDE KORKUT","ERGENEKON DESTANI","EDEB??YAT","EFSANE","MAZMUN","ESK??");
        Tabuu tabuu036 = new Tabuu("HZ. MUHAMMED (S.A.V)","ISLAM","PEYGAMBER","EM??N","ALLAH (C.C)","AH??RET");
        Tabuu tabuu037 = new Tabuu("G??RDAP","DEN??Z","BO??LUK","MARIANA","??UKUR","OLU??MAK");
        Tabuu tabuu038 = new Tabuu("HAYAT","YA??AM","D??NG??","ZAMAN","DUYGU","GE????RMEK");
        Tabuu tabuu039 = new Tabuu("??AHAN G??KBAHAR","RECEP ??VED??K","G??LMEK","EZG?? MOLA","KAYHAN","OSMAN PAZARLAMA");
        Tabuu tabuu040 = new Tabuu("SALLANMAK","SALINCAK","PARK","??OCUK","R??ZGAR","HAVA");
        Tabuu tabuu041 = new Tabuu("TAKIM","MA??","OYUN","FUTBOL","VOLEYBOL","HENTBOL");
        Tabuu tabuu042 = new Tabuu("BORNOZ","TAKIM","HAVLU","YIKANMAK","BANYO","G??YMEK");
        Tabuu tabuu043 = new Tabuu("A??LAMAK","BEBEK","??NSAN","HAYVAN","ACI","NE??E");
        Tabuu tabuu044 = new Tabuu("EHEMM??YET","??NEM","TECR??BE","??NCEL??K","ZORLUK","??ST");
        Tabuu tabuu045 = new Tabuu("SIRADAN","ORTALAMA","NORMAL","VARSAYILAN","MEVCUT","??NSAN");
        Tabuu tabuu046 = new Tabuu("VER??TABANI","SQL","SQLITE","FIREBASE","OFFLINE","DEPOLAMAK");
        Tabuu tabuu047 = new Tabuu("TOPLANTI","KONFERANS","??YE","????RKET","GRUP","TOPLULUK");
        Tabuu tabuu048 = new Tabuu("CEMRE","HAVA","SU","TOPRAK","BAHAR","D????MEK");
        Tabuu tabuu049 = new Tabuu("KAZAK","KI??","??RMEK","Y??N","KALIN","G??YS??");
        Tabuu tabuu050 = new Tabuu("S??KMEK","BOZMAK","OYNAMAK","ALET","IRDAVAT","TORNAV??DA");
        Tabuu tabuu051 = new Tabuu("KURGU","MONTAJ","OYNAMA","FILM","??NL??","B??L??M");
        Tabuu tabuu052 = new Tabuu("??K??LEM","SE??ENEK","B??R","KARAR","ARADA KALMAK","EM??N OLMAK");
        Tabuu tabuu053 = new Tabuu("KATEGOR??","AYIRMAK","TOPLU","GRUP","FILM","D??Z??");
        Tabuu tabuu054 = new Tabuu("NE??ET ERTA??","BOZKIR","??ARKI","NEV??EH??R","SAZ","S??YLEMEK");
        Tabuu tabuu055 = new Tabuu("TU??","DAKT??LO","KLAVYE","TELEFON","B??LG??SAYAR","D????ME");
        Tabuu tabuu056 = new Tabuu("GICIK OLMAK","HO??LANMAK","??FKE","SEVMEMEK","S??N??R BOZUCU","K??T??");
        Tabuu tabuu057 = new Tabuu("KONSEY","GRUP","M??LLET VEK??L??","G??VENL??K","TOPLUM","MECL??S");
        Tabuu tabuu058 = new Tabuu("ASKER","BORDO BEREL??","G??VENL??K","SEVER","??LKE","D????MAN");
        Tabuu tabuu059 = new Tabuu("MICHAEL JORDAN","HEL??KOPTER","KAZA","KIZ","??LMEK","BASKETBOL");
        Tabuu tabuu060 = new Tabuu("ARA BULUCU OLMAK","D??ZELTMEK","SEVMEK","ARKADA??","DOST","ARASINDA");
        Tabuu tabuu061 = new Tabuu("U D??N??????","KAV??AK","DAR KAV??S","GEN???? KAV??S","GER??","YAPMAK");
        Tabuu tabuu062 = new Tabuu("MUSTAFA KEMAL ATAT??RK","CUMHUR??YET","T??RK??YE","ASKER","AKDEN??Z","OSMANLI");
        Tabuu tabuu063 = new Tabuu("BAH??EL??","PART??","MHP","P??SKEV??T","DAVA","KURMAK");
        Tabuu tabuu064 = new Tabuu("T??KS??NMEK","????RENMEK","YEMEK","SEVMEMEK","BULANMAK","MIDE");
        Tabuu tabuu065 = new Tabuu("BAKIMSIZ","KEND??","AYNA","Y??Z","??ZENS??Z","??LG??");

        //Yorum yapan kullan??c?? i??in a??a????daki a????lacak
        Tabuu tabuu066 = new Tabuu("AY??EKADIN","FASULYE","SEBZE","YE????L","??S??M","KADIN");
        Tabuu tabuu067 = new Tabuu("HOPARL??R","RADYO","M??Z??K","SES","B??LG??SAYAR","TV");
        Tabuu tabuu068 = new Tabuu("ABS??RD","GEREKS??Z","SA??MA","ANLAMSIZ","UYGUNSUZ","KOMED??");
        Tabuu tabuu069 = new Tabuu("A??IT","A??LAMAK","CENAZE","DO??U","BA??IRMAK","T??RK??");
        Tabuu tabuu070 = new Tabuu("G??LLE","TOP","SPOR","S??YAH","OL??MP??YAT","OYUN");
        Tabuu tabuu071 = new Tabuu("EFES","??ZM??R","ANT??K","KENT","TAR??H??","ESER");
        Tabuu tabuu072 = new Tabuu("BESTE","??ARKI","M??Z??K","??ALMAK","EZG??","T??RK??");
        Tabuu tabuu073 = new Tabuu("DEKORASYON","EV","D??ZAYN","TASARIM","M??MAR","YEN??L??K");
        Tabuu tabuu074 = new Tabuu("GALATASARAY","FERNANDO MUSLERA","FUTBOL","MA??","BASKETBOL","SPOR");
        Tabuu tabuu075 = new Tabuu("NBA","KOBE BRYANT","H??DAYET T??RKO??LU","CED?? OSMAN","BASKETBOL","FUTBOL");
        Tabuu tabuu076 = new Tabuu("ADAPT??R","UYUM","??L????K??","BATARYA","TAKMAK","KABLO");
        Tabuu tabuu077 = new Tabuu("H??PODROM","KO??U","VEL?? EFEND??","AT","YARI??","JOKEY");
        Tabuu tabuu078 = new Tabuu("HAVUZ","V??LLA","Y??ZMEK","DEN??Z","OTEL","SU");
        Tabuu tabuu079 = new Tabuu("PS??KOANAL??Z","TELK??N","METOT","??KNA","??ZZET G??LL??","SEANS");
        Tabuu tabuu080 = new Tabuu("ORYANTAL","TANYEL??","M??Z??K","ORKESTRA","GRUP","MELOD??");
        Tabuu tabuu081 = new Tabuu("??ARKICI","M??Z??K","SANAT","MURAT BOZ","HAD??SE","MF??");
        Tabuu tabuu082 = new Tabuu("????P??ATAN","EVLENMEK","????FT","ARACI OLMAK","B??R ARAYA GELMEK","MESLEK");
        Tabuu tabuu083 = new Tabuu("DRAKULA","FRANKE??TAYN","VAMP??R","KURT ADAM","YARASA","KAN");
        Tabuu tabuu084 = new Tabuu("SANDAL","TEKNE","VAPUR","GONDOL","DEN??Z","SEYEHAT");
        Tabuu tabuu085 = new Tabuu("AL?? KU????U","MATEMAT??K","B??LG??N","AL??M","OSMANLI","EDEB??YAT");
        Tabuu tabuu086 = new Tabuu("KOZA","KANAT","B??CEK","KELEBEK","TIRTIL","ARI");
        Tabuu tabuu087 = new Tabuu("H??CR?? TAKV??M","M??LAD??","PEYGAMBER","AY","ZAMAN","H??CRET");
        Tabuu tabuu088 = new Tabuu("REDKIT","????ZG?? F??LM","TELEV??ZYON","ESK??","??ZLEMEK","SEYRETMEK");
        Tabuu tabuu089 = new Tabuu("KAZAND??B??","TATLI","MARKET","FIRIN","TR??LE??E","S??T");
        Tabuu tabuu090 = new Tabuu("B??AT ETMEK","KABUL ETMEK","UYMAK","KABULLENMEK","D??N??","S??Z");
        Tabuu tabuu091 = new Tabuu("ENG??N ALTAN D??ZYATAN","D??R??L???? ERTU??RUL","SARI??IN","SA??","D??Z??","YAKI??IKLI");
        Tabuu tabuu092 = new Tabuu("??A??ATAY ULUSOY","????ERDE","ARAS BULUT ??YNEML??","HAKAN MUHAFIZ","DEL??BAL","SERENAY SARIKAYA");
        Tabuu tabuu093 = new Tabuu("CEM YILMAZ","KOMEDYEN","K??PE","BO??AZ??????","??STANBUL","TEK K??????");
        Tabuu tabuu094 = new Tabuu("HIPOKRAT","HIPPOKRATES","YUNAN??STAN","TIP","??STANK??Y","B??L??M");
        Tabuu tabuu095 = new Tabuu("TA??INAB??L??R D??SK","HAR??C??","SATA","VER??","DEPOLAMAK","HARD DISK");
        Tabuu tabuu096 = new Tabuu("PART??","B??RL??KTEL??K","ARKADA??","??ZEL","G??N","YABANCI");
        Tabuu tabuu097 = new Tabuu("TEBE????R","KARA TAHTA","BEYAZ","K??RE??","??UBUK","OKUL");
        Tabuu tabuu098 = new Tabuu("OL??MP??YAT","OYUN","MADALYA","ESK??","GE??M????","MA??");
        Tabuu tabuu099 = new Tabuu("K??LT??R","M??LLET","DEVLET","GELENEK","NES??L","M??SAF??R");


        Tabuu tabuu = new Tabuu("HAL EK??","??EK??M EK??","-?? HAL??","-DE HAL??","-DEN HAL??","-E HAL??");
        Tabuu tabuu1 = new Tabuu("YAPIM EK??","EK","KEL??ME","K??K","T??REM????","YAPI");
        Tabuu tabuu2 = new Tabuu("F????L??MS??","SIFAT F????L","??S??M F????L","ZARF F????L","F????L","EK");
        Tabuu tabuu3 = new Tabuu("E?? SESL??","YAZILI??","OKUNU??","AYNI","S??ZC??K","SESTE??");
        Tabuu tabuu4 = new Tabuu("ZIT ANLAM","KAR??IT","S??ZC??K","??RNEK","B??RB??R??NE","MANA");
        Tabuu tabuu5 = new Tabuu("F????L","????","OLU??","HAREKET","EYLEM","S??ZC??K");
        Tabuu tabuu6 = new Tabuu("UYAK","??????R","D??ZE","BENZERL??K","KAF??YE","SES");
        Tabuu tabuu7 = new Tabuu("S??ZL??K","ANLAM","KEL??ME","S??ZC??K","A??IKLAMA","L??GAT");
        Tabuu tabuu8 = new Tabuu("KU??BAKI??I","HAR??TA","TEPE","YUKARI","G??RMEK","KROK??");
        Tabuu tabuu9 = new Tabuu("OKKA","A??IRLIK","??L????","B??R??M","K??LO","TARTI");
        Tabuu tabuu10 = new Tabuu("K??K","S??ZC??K","EK","YAPIM","??EK??M","KEL??ME");
        Tabuu tabuu11 = new Tabuu("??????R","??A??R","MISRA","D??ZE","KITA","D??RTL??K");
        Tabuu tabuu12 = new Tabuu("FABL","HAYVAN","LA FONTAINE","MASAL","??NSAN","H??KAYE");
        Tabuu tabuu13 = new Tabuu("??YK??","H??KAYE","KAHRAMAN","YAZAR","YER","K??TAP");
        Tabuu tabuu14 = new Tabuu("HAL EK??","??EK??M EK??","-?? HAL??","-DE HAL??","-DEN HAL??","-E HAL??");
        Tabuu tabuu15 = new Tabuu("YAPIM EK??","EK","KEL??ME","K??K","T??REM????","YAPI");
        Tabuu tabuu16 = new Tabuu("F????L??MS??","SIFAT F????L","??S??M F????L","ZARF F????L","F????L","EK");
        Tabuu tabuu17 = new Tabuu("E?? SESL??","YAZILI??","OKUNU??","AYNI","S??ZC??K","SESTE??");
        Tabuu tabuu18 = new Tabuu("ZIT ANLAM","KAR??IT","S??ZC??K","??RNEK","B??RB??R??NE","MANA");
        Tabuu tabuu19 = new Tabuu("MECAZ","KEL??ME","S??ZC??K","GER??EK ANLAM","YAN ANLAM","TER??M ANLAM");
        Tabuu tabuu20 = new Tabuu("??ZNE","C??MLE","????","K??????","VARLIK","????E");
        Tabuu tabuu21 = new Tabuu("BENZE??ME","SERT","??NS??Z","FISTIK??I ??AHAP","GeCiD","KURAL");
        Tabuu tabuu22 = new Tabuu("MASAL","OLA??AN??ST??","KAHRAMAN","YAZAR","KELO??LAN","K??TAP");
        Tabuu tabuu23 = new Tabuu("T??REM????","S??ZC??K","KEL??ME","YAPIM EK??","??EK??M EK??","YAPI");
        Tabuu tabuu24 = new Tabuu("??NS??Z YUMU??AMASI","KET??AP","HARF","SES","??NL??","SERTLE??ME");
        Tabuu tabuu25 = new Tabuu("FAY","HAT","DEPREM","KIRILMAK","SARSILMAK","??STANBUL");
        Tabuu tabuu26 = new Tabuu("KONU??TURMA","??NTAK","HAYVAN","CANSIZ","VARLIK","SANAT");
        Tabuu tabuu27 = new Tabuu("??NER??","TEKL??F","????Z??M","YOL","C??MLE","??Y?? OLUR");
        Tabuu tabuu28 = new Tabuu("K??????MSEME","N??TEL??K","DE??ERS??Z","YARGI","C??MLE","AZIMSAMA");
        Tabuu tabuu29 = new Tabuu("??K??LEM","KARARSIZ","C??MLE","DURUM","ACABA","TERC??H");
        Tabuu tabuu30 = new Tabuu("VARSAYIM","OLMAMI??","TUT K??","FARZ ET","D??YEL??M","C??MLE");



        Tabuu tabuu31 = new Tabuu("AKS??","S??N??RL??","TERS","ZIT","ASAB??","HUYSUZ");
        Tabuu tabuu32 = new Tabuu("VEZNE","BANKA","PARA","??DEME","ALMAK","MUHASEBE");
        Tabuu tabuu33 = new Tabuu("OLUK","AKMAK","SU","YA??MUR","SA??AK","BORU");
        Tabuu tabuu34 = new Tabuu("CEPHE","SAVA??","ORDU","ASKER","SALDIRMAK","BARI??");
        Tabuu tabuu35 = new Tabuu("??EV??K","KUVVET","POL??S","ASKER","G????","HIZLI");
        Tabuu tabuu36 = new Tabuu("YAMA??","U??URUM","Y??KSEK","DA??","D??K","PARA????T");
        Tabuu tabuu37 = new Tabuu("P??L","KALEM","??NCE","KUMANDA","EL FENER??","FEN");
        Tabuu tabuu38 = new Tabuu("Z??RAAT","TARIM","????FT????","TOPRAK","HAYVAN","BANKA");
        Tabuu tabuu39 = new Tabuu("PROSPEKT??S","??LA??","ECZANE","OKUMAK","??????NDEK??LER","KULLANMAK");
        Tabuu tabuu40 = new Tabuu("ESK??MO","KUTUP","BUZUL","KAR","SO??UK","BALIK");
        Tabuu tabuu41 = new Tabuu("HAMAK","YATMAK","A??A??","SALLANMAK","KURMAK","P??KN??K");
        Tabuu tabuu42 = new Tabuu("GADDAR","ACIMASIZ","SERT","KATI","MERHAMET","??NSAF");
        Tabuu tabuu43 = new Tabuu("??ADIR","KAMP","UYKU TULUMU","KURMAK","DO??A","TAT??L");
        Tabuu tabuu44 = new Tabuu("NADAS","TARLA","EKMEK","BIRAKMAK","D??NLEND??RMEK","????FT????");
        Tabuu tabuu45 = new Tabuu("HASIR","SEPET","??APKA","PLAJ","DEN??Z","SERMEK");
        Tabuu tabuu46 = new Tabuu("BUKELAMUN","HAYVAN","RENK","KERTENKELE","DE??????MEK","S??R??NGEN");
        Tabuu tabuu47 = new Tabuu("KONVOY","ARABA","G??TMEK","D??Z??LMEK","ARKA","D??????N");
        Tabuu tabuu48 = new Tabuu("TAKUNYA","TAHTA","TERL??K","AYAKKABI","G??YMEK","HAMAM");
        Tabuu tabuu49 = new Tabuu("SERVET","KAZANMAK","ZENG??NL??K","MAL","M??LK","PARA");
        Tabuu tabuu50 = new Tabuu("TEDAR??K","BULMAK","SA??LAMAK","MALZEME","ETMEK","HAZIRLIK");
        Tabuu tabuu51 = new Tabuu("TABURE","OTURMAK","SANDALYE","KOLTUK","YEMEKHANE","SIRT");



        Tabuu tabuu52 = new Tabuu("EREZYON","Toprak","Kayma","A??a??","Heyelan","TEMA");
        Tabuu tabuu53 = new Tabuu("??AMA??IR","Kirli","Y??kamak","Makine","Deterjan","Giymek");
        Tabuu tabuu54 = new Tabuu("NUMUNE","??rnek","Vermek","Tahlil","Yemek","K??????K");
        Tabuu tabuu55 = new Tabuu("V??RAN","Eski","Y??k??k","Harap","Ev","K??hne");
        Tabuu tabuu56 = new Tabuu("KASVET","S??k??nt??","Gam","Karanl??k","Ayd??nl??k","Ne??eli");
        Tabuu tabuu57 = new Tabuu("M????O","Tayfa","Gemi","Kaptan","Deniz","Yard??mc??");
        Tabuu tabuu58 = new Tabuu("KAB??N","Giyinmek","DU??","Denemek","Ma??aza","K??yafet");
        Tabuu tabuu59 = new Tabuu("PORS??YON","Yemek","Tabak","Lokanta","Yar??m","Restoran");
        Tabuu tabuu60 = new Tabuu("DOZ","Miktar","??LA??","Doktor","RE??ETE","A??IRI");
        Tabuu tabuu61 = new Tabuu("D??YAR","Memleket","Vatan","Dolasmak","Gezmek","??s??k Veysel");
        Tabuu tabuu62 = new Tabuu("BO??BO??AZ","Konu??mak","Geveze","S??r","S??ylemek","Anlatmak");
        Tabuu tabuu63 = new Tabuu("MONOTON","Ayn??","S??k??c??","S??radan","Benzer","Rutin");
        Tabuu tabuu64 = new Tabuu("YADIRGAMAK","Garip","Tuhaf","Kabullenmek","??A??IRMAK","DAVRANI??");
        Tabuu tabuu65 = new Tabuu("ZEYBEK","Atat??rk","Oyun","Ege","Efe","Sar??");
        Tabuu tabuu66 = new Tabuu("FESHETMEK","ANTLA??MA","S??ZLE??ME","??ptal","GE??ERS??Z","Bozmak");
        Tabuu tabuu67 = new Tabuu("PARANOYA","????phe","Ak??l","Ruh","Hastal??k","Deli");
        Tabuu tabuu68 = new Tabuu("G??ZERG??H","Yol","Araba","Rota","Servis","Takip Etmek");
        Tabuu tabuu69 = new Tabuu("PERFORMANS","Degerlendirme","Basar??","Ders","??dev","Y??ksek");
        Tabuu tabuu70 = new Tabuu("UCUZLUK","??ndirim","Pahal??","Y??zde","Fiyat","Vitrin");
        Tabuu tabuu71 = new Tabuu("ABLUKA","Etraf","KU??ATMA","SAVA??","??evirmek","D????MAN");
        Tabuu tabuu72 = new Tabuu("VERES??YE","PE????N","BOR??","Sat??n Almak","??demek","Defter");
        Tabuu tabuu73 = new Tabuu("PAT??KA","Ke??i Yolu","Orman","Kestirme","Y??r??mek","DA??");
        Tabuu tabuu74 = new Tabuu("BU??U","Buhar","Cam","Su","S??cak","Araba");
        Tabuu tabuu75 = new Tabuu("??AVDAR","Arpa","Tah??l","BU??DAY","Kepek","Ekmek");
        Tabuu tabuu76 = new Tabuu("NAD??R","Zor","Az","Bulmak","S??k","??ok");
        Tabuu tabuu77 = new Tabuu("MIZIK??I","??ocuk","K??smek","Dar??lmak","Bozmak","Oyun");
        Tabuu tabuu78 = new Tabuu("ES??R","Mahk??m","SAVA??","D????MEK","Tutsak","Kamp");

        Tabuu tabuu79 = new Tabuu("YAD??GAR","Aile","Hat??ra","DE??ERL??","Miras","B??rakmak");
        Tabuu tabuu80 = new Tabuu("SISKA","ZAYIF","??NCE","??EL??MS??Z","??????MAN","HASTA");
        Tabuu tabuu81 = new Tabuu("SU??ST??MAL","??Y??","N??YET","KULLANMAK","FAYDALANMAK","K??T??");
        Tabuu tabuu82 = new Tabuu("MACUN","CAM","PENCERE","TUTMAK","KENAR","MES??R");
        Tabuu tabuu83 = new Tabuu("TECR??BE","????","KAZANMAK","??ALI??MAK","DENEY??M","YIL");
        Tabuu tabuu84 = new Tabuu("ARIZA","BOZUK","TAM??R","??ALI??MAK","TELEFON","ELEKTR??K");
        Tabuu tabuu85 = new Tabuu("STAJ","????RENC??","??ALI??MAK","??N??VERS??TE","TECR??BE","????");
        Tabuu tabuu86 = new Tabuu("SIRIK","UZUN","ATLAMAK","BOY","ATLET??ZM","FASULYE");
        Tabuu tabuu87 = new Tabuu("KAOS","KARI??IKLIK","ORTAM","KARGA??A","D??ZEN","YARATMAK");
        Tabuu tabuu88 = new Tabuu("FULAR","E??ARP","BA??LAMAK","BA??","BOYUN","KADIN");
        Tabuu tabuu89 = new Tabuu("PLAKET","??D??L","T??REN","BA??ARI","VERMEK","TE??EKK??R");
        Tabuu tabuu90 = new Tabuu("F??RAR","KA??AK","HAP??S","ASKER","MAHKUM","ETMEK");
        Tabuu tabuu91 = new Tabuu("HALAY","D??????N","??EKMEK","OYNAMAK","MEND??L","KOL");
        Tabuu tabuu92 = new Tabuu("H??LE","ALDATMAK","KANDIRMAK","OYUN","YAPMAK","KUMAR");
        Tabuu tabuu93 = new Tabuu("PASAKLI","TEM??Z","T??T??Z","K??RL??","D??ZENL??","KARI??IK");
        Tabuu tabuu94 = new Tabuu("UYANIK","AKILLI","ZEK??","A??IKG??Z","KURNAZ","SAF");
        Tabuu tabuu95 = new Tabuu("??HALE","BELED??YE","G??RMEK","A??MAK","YOLSUZLUK","KAZANMAK");
        Tabuu tabuu96 = new Tabuu("????TAH","ACIKMAK","KES??LMEK","A??MAK","LEZZET","YEMEK");
        Tabuu tabuu97 = new Tabuu("AJANDA","DEFTER","????","YAZMAK","G??N","TOPLANTI");
        Tabuu tabuu98 = new Tabuu("M??SVEDDE","KARALAMA","NOT ALMAK","YAZMAK","KA??IT","TEM??Z");
        Tabuu tabuu99 = new Tabuu("FLAMA","BAYRAK","????GEN","OKUL","??ZC??","T??REN");
        Tabuu tabuu100 = new Tabuu("S??FTAH","??LK","G??N","SATMAK","MAL","ALI??VER????");


        Tabuu tabuu101 = new Tabuu("TAL??MAT","EM??R","VERMEK","FATURA","BANKA","OTOMAT??K ??DEME");
        Tabuu tabuu102 = new Tabuu("??UVAL","TORBA","DOLDURMAK","YEM","UN","KOYMAK");
        Tabuu tabuu103 = new Tabuu("G????MEN","M??LTEC??","V??ZE","SO??UK","KU??","SICAK");
        Tabuu tabuu104 = new Tabuu("YELPAZE","SICAK","R??ZGAR","YAZ","KADIN","SALLAMAK");
        Tabuu tabuu105 = new Tabuu("YABAN??","VAH????","??LKEL","HAYAT","ORMAN","HAYVAN");
        Tabuu tabuu106 = new Tabuu("BAMBU","SAZLIK","MOB??LYA","A??A??","MASA","SANDALYE");
        Tabuu tabuu107 = new Tabuu("AKROST????","??????R","MISRA","??S??M","??LK","KITA");
        Tabuu tabuu108 = new Tabuu("YAYIK","AYRAN","SU","YO??URT","??ALKALAMAK","S??T");
        Tabuu tabuu109 = new Tabuu("K??HNE","ESK??","TAR??H??","YIKILMAK","B??NA","HARABE");
        Tabuu tabuu110 = new Tabuu("K??REMEK","KAR","EV","YOL","BUZ","KI??");
        Tabuu tabuu111 = new Tabuu("MESA??","SAAT","????","FAZLA","AK??AM","KALMAK");
        Tabuu tabuu112 = new Tabuu("M??SK??N","TEMBEL","UYU??UK","YAVA??","A??IR","KED??");
        Tabuu tabuu113 = new Tabuu("KIVILCIM","ATE??","K??BR??T","??AKMAK","TA??","S??RTMEK");
        Tabuu tabuu114 = new Tabuu("PRAT??K","KOLAY","ZEKA","??ABUK","HIZLI","????Z??M");
        Tabuu tabuu115 = new Tabuu("F??DAN","A??A??","B??Y??MEK","K??????K","ORMAN","D??KMEK");
        Tabuu tabuu116 = new Tabuu("MAN??","KISA","??????R","EDEB??YAT","SAKIZ","ENGEL");
        Tabuu tabuu117 = new Tabuu("??IRA","ATE??","YAKMAK","ODUN","TAHTA","??AY");
        Tabuu tabuu118 = new Tabuu("HEYBETL??","Y??CE","DA??","B??Y??K","Y??KSEK","??R??");
        Tabuu tabuu119 = new Tabuu("BONK??R","EL?? A??IK","C??MERT","PARA","G??NL?? ZENG??N","HARCAMAK");
        Tabuu tabuu120 = new Tabuu("ULEMA","B??LG??N","OSMANLI","D??N","AL??M","HOCA");
        Tabuu tabuu121 = new Tabuu("??Z??EK??M","SELFIE","TELEFON","KEND??","FOTO??RAF","POZ");
        Tabuu tabuu122 = new Tabuu("K??????LE??T??RME","??NSAN","FABL","HAYVAN","VARLIK","KONU??TURMA");
        Tabuu tabuu123 = new Tabuu("HO??G??R??","MEVLANA","ANLAYI??","EMPAT??","NE OLURSAN","GEL");
        Tabuu tabuu124 = new Tabuu("EMPAT??","KEND??N??","BA??KASI","YER??NE","KOYMA","D??????NME");
        Tabuu tabuu125 = new Tabuu("ANAHTAR","K??L??T","METAL","KASA","KAPI","????L??NG??R");
        Tabuu tabuu126 = new Tabuu("KED??","PAT??","FARE","T??Y","KUYRUK","HAYVAN");
        Tabuu tabuu127 = new Tabuu("KAPAN","AV","KURT","HAYVAN","TUZAK","FARE");
        Tabuu tabuu128 = new Tabuu("DERG??","GAZETE","MECMUA","MAKALE","YAZI","KAPAK");
        Tabuu tabuu129 = new Tabuu("DOST","G??VEN","SAM??M??","D??R??ST","ARKADA??","AHLAKLI");
        Tabuu tabuu130 = new Tabuu("BA??LAMA","T??RK??","M??Z??K","SAZ","TEL","AKORT");
        Tabuu tabuu131 = new Tabuu("T??YATRO","OYUNCU","SAHNE","PERDE","OYUN","SUFL??R");
        Tabuu tabuu132 = new Tabuu("MALA","??N??AAT","DUVAR","USTA","SIVA","????MENTO");
        Tabuu tabuu133 = new Tabuu("G??KKU??A??I","RENKL??","HAVA","G??NE??","YA??MUR","EBEM");
        Tabuu tabuu134 = new Tabuu("KELEBEK","RENKL??","U??MAK","TIRTIL","KOZA","HAYVAN");
        Tabuu tabuu135 = new Tabuu("FERMUAR","PANTOLON","MONT","G??YS??","KIYAFET","KOT");
        Tabuu tabuu136 = new Tabuu("BALMUMU","MUM","ER??MEK","HEYKEL","M??ZE","BAL");
        Tabuu tabuu137 = new Tabuu("PARA????T","U??AK","ATLAMAK","U??MAK","BALON","HAVA");
        Tabuu tabuu138 = new Tabuu("MANGALA","OYUN","OSMANLI","KUYU","TA??","HAZ??NE");
        Tabuu tabuu139 = new Tabuu("SATRAN??","??AH-MAT","KALE","VEZ??R","P??YON","F??L");
        Tabuu tabuu140 = new Tabuu("ADEM ELMASI","ERKEK","GIRTLAK","??IKINTI","BO??AZ","HAVVA");
        Tabuu tabuu141 = new Tabuu("ANT??KA","M??ZAYEDE","ZENG??N","ESK??","TABLO","TAR??H??");
        Tabuu tabuu142 = new Tabuu("KUMBARA","PARA","B??R??KT??RMEK","YATIRIM","BANKA","SAKLAMAK");
        Tabuu tabuu143 = new Tabuu("P????MANLIK","HATA","??Z??LMEK","YANLI??","KE??KE","SON");
        Tabuu tabuu144 = new Tabuu("KABZIMAL","MEYVE","SEBZE","HAL","SATMAK","ARACI");
        Tabuu tabuu145 = new Tabuu("ARMA??AN","HED??YE","VERMEK","ALMAK","DO??UM G??N??","SEV??ND??RMEK");
        Tabuu tabuu146 = new Tabuu("DUY","TAVAN","LAMBA","I??IK","??????TMEK","ANAHTAR");
        Tabuu tabuu147 = new Tabuu("N??HALE","ALTLIK","TENCERE","??AYDANLIK","SICAK","TEZGAH");
        Tabuu tabuu148 = new Tabuu("KORN????","TAVAN","PERDE","ASMAK","PENCERE","CAM");
        Tabuu tabuu149 = new Tabuu("TIRABZAN","MERD??VEN","KORKULUK","??NMEK","??IKMAK","B??NA");
        Tabuu tabuu150 = new Tabuu("??AMDAN","MUM","I??IK","AYDINLIK","YAKMAK","S??S");
        Tabuu tabuu151 = new Tabuu("BANDANA","MASTERCHEF","TAKMAK","G??YS??","EM??R","ALI??VER????");


        Tabuu tabuu152 = new Tabuu("HAL??S??NASYON","HAYAL","GER??EK","G??RMEK","??LL??ZYON","SERAP");
        Tabuu tabuu153 = new Tabuu("CEMRE","D????MEK","HAVA","TOPRAK","SU","BAHAR");
        Tabuu tabuu154 = new Tabuu("??AYLAK","ACEM??","DENEY??M","TECR??BE","USTA","YEN??");
        Tabuu tabuu155 = new Tabuu("HARABE","YIKIK","ESK??","TAR??H??","EFES","GEZMEK");
        Tabuu tabuu156 = new Tabuu("BASK??L","TARTI","K??LO","A??IR","??L????","HAF??F");
        Tabuu tabuu157 = new Tabuu("KOORD??NAT","YER","ENLEM","BOYLAM","BEL??RTMEK","VERMEK");
        Tabuu tabuu158 = new Tabuu("AV??ZE","LAMBA","KR??STAL","TAVAN","I??IK","AYDINLIK");
        Tabuu tabuu159 = new Tabuu("KURNA","HAMAM","YIKANMAK","SU","TELLAK","G??BEK TA??I");
        Tabuu tabuu160 = new Tabuu("K??SMEK","DARILMAK","KIZMAK","KONU??MAK","TARTI??MAK","KAVGA");
        Tabuu tabuu161 = new Tabuu("HECE","KEL??ME","HARF","SES","OKUMAK","YAZI");
        Tabuu tabuu162 = new Tabuu("AHESTE","YAVA??","A??IR","HIZLI","DURGUN","TEMBEL");
        Tabuu tabuu163 = new Tabuu("KAR??ILA??TIRMA","KIYASLAMA","MUKAYESE","BENZERL??K","FARKLILIK","??K??");
        Tabuu tabuu164 = new Tabuu("TER??M","KEL??ME","ANLAM","B??L??M","SANAT","KAVRAM");
        Tabuu tabuu165 = new Tabuu("A??AMALILIK","G??DEREK","G??TT??K??E","KADEME","C??MLE","ANLAM");
        Tabuu tabuu166 = new Tabuu("K??P","D??LEK","F????L","HABER","B??LD??RME","Y??KLEM");
        Tabuu tabuu167 = new Tabuu("EK F????L","EYLEM","??S??M","B??RLE????K","??ART","Y??KLEM");
        Tabuu tabuu168 = new Tabuu("ZARF","BEL??RTE??","ZAMAN","YER - Y??N","DURUM","ZAMAN");
        Tabuu tabuu169 = new Tabuu("NOKTA","C??MLE","SON","????ARET","TAR??H","SAAT");
        Tabuu tabuu170 = new Tabuu("SOMUT","DUYU","ALGILAMAK","SOYUT","??S??M","G??R??LEN");
        Tabuu tabuu171 = new Tabuu("SOYUT","DUYU","ALGILAMAK","G??R??LMEYEN","??S??M","SOMUT");
        Tabuu tabuu172 = new Tabuu("ZAM??R","??S??M","????ARET","K??????","ADIL","KEL??ME");
        Tabuu tabuu173 = new Tabuu("SIFAT","??N AD","N??TELEME","??S??M","ZAM??R","????ARET");
        Tabuu tabuu174 = new Tabuu("OLASILIK","C??MLE","ANLAM","??HT??MAL","BELK??","OLAB??L??R");
        Tabuu tabuu175 = new Tabuu("HAYIFLANMA","P????MANLIK","??Z??NT??","GE??M????","C??MLE","ANLAM");
        Tabuu tabuu176 = new Tabuu("???? NOKTA","NOKTALAMA","SON","TAMAMLANMAMI??","??STENMEYEN","C??MLE");
        Tabuu tabuu177 = new Tabuu("??DEV","DERS","??ALI??MAK","OKUL","????RETMEN","EV");
        Tabuu tabuu178 = new Tabuu("ABARTMA","M??BALA??A","A??IRI","FARKLI","G??STERMEK","OLDU??UNDAN");
        Tabuu tabuu179 = new Tabuu("BENZETME","BENZEYEN","BENZET??LEN","G??B??","G????L??","G????S??Z");
        Tabuu tabuu180 = new Tabuu("KO??UL","??ART","NEDEN","C??MLE","E??ER","SONU??");
        Tabuu tabuu181 = new Tabuu("ANLAM KAYMASI","F????L","K??P","ZAMAN","ANLAM","EYLEM");
        Tabuu tabuu182 = new Tabuu("ANLATIM B??????M??","BET??MLEME","??YK??LEME","TARTI??MACI","A??IKLAYICI","PARAGRAF");
        Tabuu tabuu183 = new Tabuu("PARAGRAF","SINAV","SORU","ANA D??????NCE","YARDIMCI D??????NCE","MET??N");
        Tabuu tabuu184 = new Tabuu("??NTERNET","ADSL","CHAT","MODEM","4.5G","MOB??L VER??");
        Tabuu tabuu185 = new Tabuu("K??RL?? SAKAL","KIL","BIRAKMAK","Y??Z","ERKEK","TRA??");
        Tabuu tabuu186 = new Tabuu("????T","TAHTA","BAH??E","TARLA","??EVRE","SARMAK");
        Tabuu tabuu187 = new Tabuu("BILL GATES","WINDOWS","OFFICE","MELINDA GATES","B??LG??SAYAR","GIRISIMCI");
        Tabuu tabuu188 = new Tabuu("SEL??UK BAYRAKTAR","??NSANSIZ HAVA ARACI","YERL??","M??LL??","SAVUNMA","DI?? G????");
        Tabuu tabuu189 = new Tabuu("ELON MUSK","AFR??KA","TESLA","NEURALINK","RECEP TAYYIP ERDOGAN","SOLAR CITY");
        Tabuu tabuu190 = new Tabuu("KLAVYE","TIK TIK","YAZMAK","ARA??","MOUSE","TELEFON");
        Tabuu tabuu191 = new Tabuu("TOGG","GIRISIM","OTOMOB??L","ARABA","YERL??","M??LL??");
        Tabuu tabuu192 = new Tabuu("??UKUR","KAS??S","CO??RAF??","DEL??K","KUYU","F??Z??K");
        Tabuu tabuu193 = new Tabuu("ZEKAT","MAL","KIRKTA B??R","ZENG??N","FAK??R","PARA");
        Tabuu tabuu194 = new Tabuu("HAC","KABE","MEKKE","TAVAF","??BADET","??HRAM");
        Tabuu tabuu195 = new Tabuu("PETROL","BENZ??N","MAZOT","YAKIT","AKARYAKIT","OTOMOB??L");
        Tabuu tabuu196 = new Tabuu("KOLONYA","L??MON","ALKOL","CORONA","DEZENFEKTAN","AS??T");
        Tabuu tabuu197 = new Tabuu("BANAB??","PEMBE","MARKET","DARK STORE","PAPEL","S??PAR????");
        Tabuu tabuu198 = new Tabuu("TEKNOLOJ??","??R??N","B??LG??SAYAR","??RETMEK","KOLAY","YEN??");
        Tabuu tabuu199 = new Tabuu("END??STR?? 4.0","SANAY?? DEVR??M??","D??RT","GEL????MEK","Y??NTEM","??RET??M");
        Tabuu tabuu200 = new Tabuu("UZAY","NASA","BLUE ORIGIN","??ST??KBAL","SATURN","METEOR");
        Tabuu tabuu201 = new Tabuu("SU","????MEK","??NSAN","ATE??","BARAJ","BARDAK");
        Tabuu tabuu202 = new Tabuu("MASTERCHEF","ACUN ILICALI","JUNIOR","TAKIM","??EF","??AMP??YON");
        Tabuu tabuu203 = new Tabuu("M??GE ANLI","KAYIP","TATLI SERT","PS??KOLOG","PS??K??YATR??ST","KADIN");
        Tabuu tabuu204 = new Tabuu("HAKAN ????K??R","FUTBOLCU","S??YASET","GALATASARAY","FENERBAH??E","SAPANCA");
        Tabuu tabuu205 = new Tabuu("KABUL ETMEK","ONAYLAMAK","TAMAM","ERMEK","??MKAN","YAPMAK");
        Tabuu tabuu206 = new Tabuu("PO??ET","PLAST??K","PARALI MARKET","KUMA??","TORBA","KOYMAK");
        Tabuu tabuu207 = new Tabuu("AVAREL","HAYDUT","UZUN","REDKIT","DALTON","????Z??K");
        Tabuu tabuu208 = new Tabuu("POLEN","BAL","ARI","B??CEK","U??MAK","SARI");
        Tabuu tabuu209 = new Tabuu("K??LYUTMAZ","HABABAM","SINIF","KOPYA","H??LE","KURAL");
        Tabuu tabuu210 = new Tabuu("VERES??YE VERMEK","SONRA","BAKKAL","ELDE ETMEK","KABUL","PARA");
        Tabuu tabuu211 = new Tabuu("V??CDAN AZABI","??EKMEK","P????MAN","ACI","DUYGU","K??T??");
        Tabuu tabuu212 = new Tabuu("NOEL","KUTLAMAK","HARAM","YILBA??I","E??LENCE","KIRMIZI");
        Tabuu tabuu213 = new Tabuu("MALTA","ADA","S??RG??N","??A??R","NAMIK KEMAL","????NAS??");
        Tabuu tabuu214 = new Tabuu("PASKALYA","HR??ST??YAN","YAHUD??","BAYRAM","YUMURTA","SENE");
        Tabuu tabuu215 = new Tabuu("GINA GELMEK","USANMAK","YAPMAK","BIKMAK","SIKILMAK","BEZD??RMEK");
        Tabuu tabuu216 = new Tabuu("BANT","KA??IT","YAPI??MAK","KOL??","JEL","PARA");
        Tabuu tabuu217 = new Tabuu("G??Z??PEK","KAVGACI","ATILGAN","CESUR","KORKMAK","??LER??");
        Tabuu tabuu218 = new Tabuu("KABARTMA TOZU","BEYAZ","FIRIN","KARBONAT","MARKET","KEK");
        Tabuu tabuu219 = new Tabuu("KULAK M??SAF??R?? OLMAK","DUYMAK","DED??KODU","KAPI","ARKA","BA??KASI");
        Tabuu tabuu220 = new Tabuu("TAL??P","EVL??L??K","??ZD??VA??","SEVG??","ADAY","M??SAF??R");
        Tabuu tabuu221 = new Tabuu("KONFERANS","EDEB??YAT","T??RK??E","HABER","TELEV??ZYON","SEMPOZYUM");
        Tabuu tabuu222 = new Tabuu("PALDIR K??LD??R","HABERS??Z","EV","M??SAF??R","DAVETS??Z","U??MAK");
        Tabuu tabuu223 = new Tabuu("EZBERLEMEK","HAFIZA","B??LG??","HAFIZ","SINAV","S??ZEL");
        Tabuu tabuu224 = new Tabuu("MIZIK??I","OYUN","D??ZENBAZ","KUMARBAZ","BOZMAK","??OCUK");
        Tabuu tabuu225 = new Tabuu("HAREKET S??STEM??","ETMEK","Y??R??MEK","KO??MAK","BO??ALTIM","DOLA??IM");
        Tabuu tabuu226 = new Tabuu("????ZMEY?? A??MAK","HADD??N?? A??MAK","SINIR","B??LMEK","????Z??K","SABIR");
        Tabuu tabuu227 = new Tabuu("HADD?? A??MAK","????ZMEY?? A??MAK","SABIRSIZ","??FKE","GER??LMEK","ARKADA??");
        Tabuu tabuu228 = new Tabuu("BA?? HEK??M","DOKTOR","AMEL??YAT","CERRAH","HASTANE","??ST");
        Tabuu tabuu229 = new Tabuu("VURDUMDUYMAZ","BAYHAN","??ARKI","GAMSIZ","D??????NMEK","HIZLI");
        Tabuu tabuu230 = new Tabuu("DE????RMEN","UN","PERVANE","TEPE","EKMEK","KURT");
        Tabuu tabuu231 = new Tabuu("SEMAVER","??AY","SICAK","P??KN??K","DEMLEMEK","TERMOS");
        Tabuu tabuu232 = new Tabuu("SAFKAN","AT","IRK","C??NS","KAR??I","AT");
        Tabuu tabuu233 = new Tabuu("ALTTAN ALMAK","POZ??T??F","YUMU??AK","KAR??I","OLUMLU","TARAF");
        Tabuu tabuu234 = new Tabuu("PARSEL","FAYANS","ARSA","B??L??K","SATMAK","PAR??A");
        Tabuu tabuu235 = new Tabuu("KIYASIYA","REKABET","M??CADELE","ZOR","KIRAN KIRANA","??EK????MEL??");
        Tabuu tabuu236 = new Tabuu("KARAOKE","M??KROFON","??ARKI","VIDEO","SOYLEMEK","S??Z");
        Tabuu tabuu237 = new Tabuu("T??K TOK","GEN??","KAPATMAK","AMER??KA","KISA VIDEO","BYTEDANCE");
        Tabuu tabuu238 = new Tabuu("KARA KUTU","SES","KAYIT","U??AK","ENKAZ","P??LOT");
        Tabuu tabuu239 = new Tabuu("SECCADE","KILMAK","NAMAZ","EZAN","??ZER??NDE","??K??ND??");
        Tabuu tabuu240 = new Tabuu("KAB??LE","AFR??KA","GRUP","S??YAH??","YA??AMAK","FAK??R");
        Tabuu tabuu241 = new Tabuu("EJDERHA","DUMAN","ATE??","VAH????","D??NAZOR","M??LYON");
        Tabuu tabuu242 = new Tabuu("POKEMON","SARI","TOP","????ZG?? F??LM","??OCUKLUK","KANAL");
        Tabuu tabuu243 = new Tabuu("ATAKAN KAYALAR","INDIGO ??OCUK","2020","ANNE","AKILLI","OLGUN");
        Tabuu tabuu244 = new Tabuu("KEHANET","B??LMEK","FAL","GELECEK","NOSTRADAMUS","KAH??N");
        Tabuu tabuu245 = new Tabuu("EKVATOR","CO??RAFYA","D??NYA","AMAZON","SIFIR","EKSEN");
        Tabuu tabuu246 = new Tabuu("SEFAM OLSUN","YED??RMEK","B??LENT ERSOY","CIMBIZ","??????RMEK","D??NYA");
        Tabuu tabuu247 = new Tabuu("MORG","HASTAHANE","CENAZE","??L??M","KEFEN","HAK");
        Tabuu tabuu248 = new Tabuu("AMBLEM","LOGO","ET??KET","REKLAM","??EK??L","MARKA");
        Tabuu tabuu249 = new Tabuu("A??DA YAPMAK","KIL","T??Y","ALMAK","BERBER","ERKEK");
        Tabuu tabuu250 = new Tabuu("SAYISAL LOTO","S??ZEL","TAL??H KU??U","??KRAM??YE","PARA","??ANS");


        //Etkinlikler i??in veya market i??in a??a????daki kelimeleri kullan

        Tabuu tabuu251 = new Tabuu("TABURE","OTURMAK","AYAKTA","PLAST??K","UCUZ","KIRILMAK");
        Tabuu tabuu252 = new Tabuu("C??PPE","HOCA","MEZUN??YET","AHMET","KIYAFET","D??N");
        Tabuu tabuu253 = new Tabuu("FL??RT ETMEK","SEVG??L??","ARKADA??","KIZ","ERKEK","EVLENMEK");
        Tabuu tabuu254 = new Tabuu("NARSIST","BE??ENMEK","K??B??R","KASILMAK","BURNU B??Y??K","SALDIRGAN");
        Tabuu tabuu255 = new Tabuu("UYUM","KARAKTER","E??","BENZER","DUYGU","D??????NCE");
        Tabuu tabuu256 = new Tabuu("AGRES??F","ATILGAN","SALDIRGAN","K??SMEK","??FKEL??","S??N??RL??");
        Tabuu tabuu257 = new Tabuu("??LA??","HAP","??URUP","HASTALIK","PS??KOLOJ??K","DOKTOR");
        Tabuu tabuu258 = new Tabuu("NIKOLA TESLA","ELEKTR??K","ARABA","DAH??","B??L??M","EDISON");
        Tabuu tabuu259 = new Tabuu("SANDIK","ESK??","PARA","ALTIN","KOYMAK","G??ZL??");
        Tabuu tabuu260 = new Tabuu("ANIME","KORE","JAPON","????ZG?? D??Z??","G??Z","YA??");
        Tabuu tabuu261 = new Tabuu("GOOGLE","ARAMAK","MOTOR","??NTERNET","YANDEX","YOUTUBE");
        Tabuu tabuu262 = new Tabuu("BREAKING BAD","D??Z??","K??MYA","SKYLER","HEISENBERG","WALTER WHITE");
        Tabuu tabuu263 = new Tabuu("DEMO","KISA","OYUN","F??LM","D??Z??","FRAGMAN");
        Tabuu tabuu264 = new Tabuu("ANDROID","????LET??M S??STEM??","GOOGLE","TELEFON","TEKNOLOJ??","WINDOWS");
        Tabuu tabuu265 = new Tabuu("KEMAL SUNAL","KOM??K","YE????L??AM","??ABAN","KOMED??","HABABAM SINIFI");
        Tabuu tabuu266 = new Tabuu("KAB??NE","MECL??S","TOPLAMAK","S??YAS??","TOPLANTI","BAKAN");
        Tabuu tabuu267 = new Tabuu("A??K","DUYGU","SEVG??L??","ERKEK","KADIN","AYRILMAK");
        Tabuu tabuu268 = new Tabuu("GOLF","SPOR","SOPA","YE????L","????MEN","ZENG??N");
        Tabuu tabuu269 = new Tabuu("DUBL??R","ART??ST","S??NEMA","F??LM","DUBLAJ","KAMERA");
        Tabuu tabuu270 = new Tabuu("KORNE??","PERDE","CAM","PLAST??K","PENCERE","ODA");
        Tabuu tabuu271 = new Tabuu("A??IK D??NYA","OYUN","GTA","ROCKSTAR","CYBERPUNK","B??LG??SAYAR");
        Tabuu tabuu272 = new Tabuu("TELAFFUZ","S??YLEN????","YABANCI","KONU??MAK","D??L","AKSAN");
        Tabuu tabuu273 = new Tabuu("LOKUM","T??RK","MUTFAK","TATLI","??EKER","BAYRAM");
        Tabuu tabuu274 = new Tabuu("??ARTEL","ELEKTR??K","EV","TRAFO","P??L","TORNAV??DA");
        Tabuu tabuu275 = new Tabuu("KARA DEL??K","UZAY","BO??LUK","NASA","DEL??K","S??YAH");
        Tabuu tabuu276 = new Tabuu("BLENDER","YEMEK","KIYMAK","KESMEK","MUTFAK","D??L??M");
        Tabuu tabuu277 = new Tabuu("MOB??LYA","??EKYAT","KOLTUK","BEYAZ E??YA","KANEPE","YATMAK");
        Tabuu tabuu278 = new Tabuu("TAKV??M","M??LAD??","AY","G??N","YIL","HAFTA");
        Tabuu tabuu279 = new Tabuu("SAHABE","PEYGAMBER","ARAP","????L","D??N","??SLAM");
        Tabuu tabuu280 = new Tabuu("ASTRONOM??","ASTRONOT","UZAY","MARS","GEZEGEN","YILDIZ");
        Tabuu tabuu281 = new Tabuu("RADYASYON","KAKT??S","????L","ELEKTRON??K","??ERNOB??L","ATOM");
        Tabuu tabuu282 = new Tabuu("VAMP??R","KAN","ALACAKARANLIK","EMMEK","F??LM","KURT");
        Tabuu tabuu283 = new Tabuu("WHATSAPP","FACEBOOK","MARK ZUCKERBERG","G??ZL??L??K","B??P","SIGNAL");
        Tabuu tabuu284 = new Tabuu("P??R?? RE??S","HAR??TA","GEM??","OSMANLI","DONANMA","AVRUPA");
        Tabuu tabuu285 = new Tabuu("DEJAVU","YA??AMAK","??NCE","GE??MEK","R??YA","OLAY");
        Tabuu tabuu286 = new Tabuu("GARDOLAP","??EKMECE","YATAK ODASI","KONSOL","ASMAK","AYNA");
        Tabuu tabuu287 = new Tabuu("STRATOSFER","BO??LUK","ATMOSFER","MEZOSFER","CO??RAF??","ASTRONOM??");
        Tabuu tabuu288 = new Tabuu("MER??DYEN","ENLEM","BOYLAM","PARALEL","D??NYA","SAAT");
        Tabuu tabuu289 = new Tabuu("A??URE","KOM??U","G??N","BAYRAM","SEBZE","MEYVE");
        Tabuu tabuu290 = new Tabuu("KARINCA","HAYVAN","ATOM","B??CEK","??ALI??KAN","????ZG?? F??LM");
        Tabuu tabuu291 = new Tabuu("M??SAK-I M??LL??","YEM??N","SINIR","??NKILAP","M??LL??","TAR??H");
        Tabuu tabuu292 = new Tabuu("KAP??T??LASYON","AYRICALIK","EKONOM??","M??SAK-I M??LL??","MAL??","OSMANLI");
        Tabuu tabuu293 = new Tabuu("G????LER B??RL??????","YASAMA","Y??R??TME","YARGI","CUMHUR??YET","KUVVET");
        Tabuu tabuu294 = new Tabuu("ROBOT","ELEKTRON??K","ANDROID","YAPAY ZEKA","YAZILIM","D????MAN");
        Tabuu tabuu295 = new Tabuu("??PUCU","AJAN","DEDEKT??F","ARAMAK","POL??S","DEL??L");
        Tabuu tabuu296 = new Tabuu("TAHS??LAT","ALACAK","PARA","??DEMEK","FATURA","BELGE");
        Tabuu tabuu297 = new Tabuu("SINAV","YAZILI","TEST","OKUL","AKADEM??SYEN","????RETMEN");
        Tabuu tabuu298 = new Tabuu("??KL??M","CO??RAF??","AKDEN??Z","DO??A","??EVRE","MEVS??M");
        Tabuu tabuu299 = new Tabuu("PIRASA","SEBZE","UZUN","SAP","??NCE","ZEYT??N YA??I");
        Tabuu tabuu300 = new Tabuu("BIYIK","SAKAL","ERKEK","T??Y","K??SE","ADAM");


        Tabuu tabuu301 = new Tabuu("TRANSFER","PARA","FUTBOL","BASKETBOL","OYUNCU","MA??");
        Tabuu tabuu302 = new Tabuu("JUSTIN BIEBER","BABY","??ARKICI","HAILEY BALDWIN","YUMMY","NEVER SAY NEVER");
        Tabuu tabuu303 = new Tabuu("D??K????","TERZ??","G??YS??","ETEK","MAK??NA","ALET");
        Tabuu tabuu304 = new Tabuu("R??YA","B??L??N??","TAB??R","HOCA","HZ. YUSUF","UYKU");
        Tabuu tabuu305 = new Tabuu("EMMANUEL MACRON","FRANSA","CUMHURBA??KANI","BRIGITTE MACRON","G??Z","NICOLAS SARKOZY");
        Tabuu tabuu306 = new Tabuu("LA CASE DE PAPEL","SU??","??SPANYOL","DENVER","NAIROBI","PROFES??R");
        Tabuu tabuu307 = new Tabuu("??N??VERS??TE","E????T??M","OKUL","Y??KSEK","AKADEM??","L??SANS");
        Tabuu tabuu308 = new Tabuu("MERKEZ","ANA","ALI??VER????","YOL","??L??E","B??NA");
        Tabuu tabuu309 = new Tabuu("S??M??K","MEND??L","GR??P","AKMAK","BURUN","MUKOZA");
        Tabuu tabuu310 = new Tabuu("KORSAN","CD","DVD","KARAY??P","F??LM","OYUN");
        Tabuu tabuu311 = new Tabuu("G??ZELL??K","KADIN","KIZ","KAR??ZMAT??K","BAYAN","RENKL??");
        Tabuu tabuu312 = new Tabuu("AFRO","SA??","AFR??KA","AMER??KAN","S??YAH??","IRK");
        Tabuu tabuu313 = new Tabuu("T??RE","GELENEK","KURAL","KANUN","DO??U","BA??LIK PARASI");
        Tabuu tabuu314 = new Tabuu("YORGAN","BATTAN??YE","YASTIK","YATMAK","GECE","YATAK");
        Tabuu tabuu315 = new Tabuu("A??I","V??R??S","KORONA","INFLUENZA","SALGIN","GR??P");
        Tabuu tabuu316 = new Tabuu("BAKLA","B??TK??","??????EK","FASULYE","TANE","SEBZE");
        Tabuu tabuu317 = new Tabuu("TE??V??K","??ZEND??RMEK","TAVS??YE","??????T","ETMEK","G??RMEK");
        Tabuu tabuu318 = new Tabuu("HAFIZA","TEKN??K","KALICI","ZEK??","B??LG??","DERS");
        Tabuu tabuu319 = new Tabuu("ATM","PARA","BANKA","Z??RAAT","??EKMEK","HALKBANK");
        Tabuu tabuu320 = new Tabuu("PARMAK","UZUV","ORGAN","AYAK","EL","TIRNAK");
        Tabuu tabuu321 = new Tabuu("??AKI","B????AK","KESK??N","KES??C??","SERSER??","??TALYAN");
        Tabuu tabuu322 = new Tabuu("K??LL??K","????MEK","ERKEK","S??GARA","K??L","T??T??N");
        Tabuu tabuu323 = new Tabuu("LEKE","??Z","S??V??LCE","AKNE","BEN","Y??Z");
        Tabuu tabuu324 = new Tabuu("KARTEL","UYU??TURUCU","MEKS??KA","SINALOA","EL-CHAPO","??ATI??MA");
        Tabuu tabuu325 = new Tabuu("SAAT","ZAMAN","GE??MEK","F??Z??K","DAK??KA","SAN??YE");
        Tabuu tabuu326 = new Tabuu("S??NGER BOB","????ZG??","F??LM","SARI","??OCUK","KARAKTER");
        Tabuu tabuu327 = new Tabuu("AFACAN","??OCUK","AZMAK","OYUN","7","YARAMAZ");
        Tabuu tabuu328 = new Tabuu("YUZARS??F","F??LM","ARAP","YUSUF","PEYGAMBER","G??ZELL??K");
        Tabuu tabuu329 = new Tabuu("OPERAT??R","HAT","SIM","KART","TELEFON","TURKCELL");
        Tabuu tabuu330 = new Tabuu("MINECRAFT","BLOK","V??DEO OYUNU","??OCUK","ZOMB??","MICROSOFT");
        Tabuu tabuu331 = new Tabuu("PUBG","EK??P","TAKIM","S??LAH","SERBEST","MOTOR");
        Tabuu tabuu332 = new Tabuu("TWITCH","CANLI","YAYIN","YOU NOW","YAYINCI","OYUN");
        Tabuu tabuu333 = new Tabuu("C??LT BAKIMI","ECZANE","G??ZELL??K","PUDRA","S??V??LCE","KADIN");
        Tabuu tabuu334 = new Tabuu("ANT??B??YOT??K","??LA??","HAP","KANSER","A??IR","B??Y??K");
        Tabuu tabuu335 = new Tabuu("TAV??YE","EK","GIDA","YANINDA","BES??N","V??TAM??N");
        Tabuu tabuu336 = new Tabuu("K??LO","GEN????","A??IR","V??CUT","??NSAN","YEMEK");
        Tabuu tabuu337 = new Tabuu("H??JYEN","TEM??ZL??K","T??T??ZL??K","ANT??BAKTER??","SABUN","DETERJAN");
        Tabuu tabuu338 = new Tabuu("FACEBOOK","INSTAGRAM","TWITTER","SOSYAL MEDYA","WHATSAPP","TOPLULUK");
        Tabuu tabuu339 = new Tabuu("UYU??TURMAK","????NE","??LA??","A??I","SIKMAK","UYU??TURUCU");
        Tabuu tabuu340 = new Tabuu("??A??","Y??ZYIL","GE??M????","GELECEK","KEBAP","ERZURUM");
        Tabuu tabuu341 = new Tabuu("EKMEK","NAN","NAN-I AZ??Z","SOMUN","ODUN","L??VA");
        Tabuu tabuu342 = new Tabuu("TANDIR","S??M??T","PO??A??A","SICAK","EKMEK","KAHVALTI");
        Tabuu tabuu343 = new Tabuu("TARZ","MODA","G??YS??","UYUM","OLMAK","YAKI??IKLI");
        Tabuu tabuu344 = new Tabuu("BERKAY HARDAL","OYUNCU","BEYAZ","D??LAN TELK??K","MELEKLER??N A??KI","??STANBULLU GEL??N");
        Tabuu tabuu345 = new Tabuu("TERM??NAT??R","YOKED??C??","KARAKTER","F??LM","KAS","B??L??M KURGU");
        Tabuu tabuu346 = new Tabuu("H??RG????","DEVE","????L","KAMBUR","B??NEK","SU");
        Tabuu tabuu347 = new Tabuu("BEBEK","ANNE","S??T","EMZ??K","??OCUK","ARABASI");
        Tabuu tabuu348 = new Tabuu("??RNEK","NUMUNE","BENZER","MODEL","AYNI","DANTEL");
        Tabuu tabuu349 = new Tabuu("POLAT ALEMDAR","KURTLAR VAD??S?? PUSU","F??L??ST??N","??LMEZ","ESK??","MEMAT??");
        Tabuu tabuu350 = new Tabuu("KANSER","HASTALIK","K??T??","T??M??R","GE??MEZ","ZOR");
        Tabuu tabuu351 = new Tabuu("NETFLIX","D??Z??","F??LM","DUBLAJ","SEZON","G??R??????M");
        Tabuu tabuu352 = new Tabuu("STEAM","OYUN","PLATFORM","GABE NEWELL","G??R??????M","DEMO");
        Tabuu tabuu353 = new Tabuu("A??LE","GEN????","??EK??RDEK","KARDE??","ANNE","BABA");
        Tabuu tabuu354 = new Tabuu("DO??RULAMAK","E-POSTA","TELEFON","G??VENL??K","B??LG??","DERLEMEK");
        Tabuu tabuu355 = new Tabuu("RADYO","TELEFON","??ALAR","ARABA","??LAH??","M??Z??K");
        Tabuu tabuu356 = new Tabuu("PLAZMA","TELEV??ZYON","LCD","EKRAN","??N??","ELEKTRON??K");
        Tabuu tabuu357 = new Tabuu("??LET??????M","CONTACT","REHBER","MESAJ","E-POSTA","??ZEL");
        Tabuu tabuu358 = new Tabuu("MA??AZA","MERKEZ","MARKET","TELEFON","MARKA","OYUN");
        Tabuu tabuu359 = new Tabuu("AVUKAT","HAK??M","MAHKEME","SAVUNMAK","KAT??L","CEZA");
        Tabuu tabuu360 = new Tabuu("EVLENMEK","KARI - KOCA","VAL??","BELGE","N????AN","D??????N");
        Tabuu tabuu361 = new Tabuu("??REME","HAYVAN","DO??URMAK","YAVRU","????FT","??O??ALMAK");
        Tabuu tabuu362 = new Tabuu("N??KLEOT??D","FOSFAT","ORGAN??K","BAZ","DNA","B??R??M");
        Tabuu tabuu363 = new Tabuu("KROMOZOM","ENGELL??","DOWN","SENDROM","44","41");
        Tabuu tabuu364 = new Tabuu("SAMANYOLU","GALAKS??","ASTRONOM","GEZEGEN","YILDIZ","BO??LUK");
        Tabuu tabuu365 = new Tabuu("ADA","YARIM","SURVIVOR","ACUN ILICALI","MASTERCHEF","G??R??????MC??");
        Tabuu tabuu366 = new Tabuu("AF??FE JALE","YILDIZ","T??YATROCU","ESK??","OYUNCU","ADAM");
        Tabuu tabuu367 = new Tabuu("TANTUN??","D??R??M","D??NER","TAVUK","??????K??FTE","HAMUR");
        Tabuu tabuu368 = new Tabuu("SENS??R","ARABA","ALARM","??ALMAK","??LER??","GER??");
        Tabuu tabuu369 = new Tabuu("DALAK","??????MEK","HERKES","A??RIMAK","M??DE","KAN");
        Tabuu tabuu370 = new Tabuu("M??SAF??RPERVER","YEMEK","KONUK","KONUT","A??IRLAMAK","??A??IRMAK");
        Tabuu tabuu371 = new Tabuu("MOLA","VERMEK","DAK??KA","SERBEST","TENEF??S","Z??L");
        Tabuu tabuu372 = new Tabuu("FATURA","SU","DO??ALGAZ","RUSYA","ELEKTR??K","AY");
        Tabuu tabuu373 = new Tabuu("EMANET","ALLAH","G??LE G??LE","G??R????MEK","OLMAK","G??TMEK");
        Tabuu tabuu374 = new Tabuu("??KRAM","BEDAVA","PARASIZ","??EKER","M??SAF??R","LOKUM");
        Tabuu tabuu375 = new Tabuu("REKLAM","UYGULAMA","TELEV??ZYON","ARA VERMEK","AZ","PROGRAM");
        Tabuu tabuu376 = new Tabuu("OTOPS??","ADL?? TIP","KADAVRA","CERRAH","AMEL??YAT","CESET");
        Tabuu tabuu377 = new Tabuu("??MER HAYYAM","??ARAP","??A??R","RUBA??","??????R","FARS");
        Tabuu tabuu378 = new Tabuu("ZONKLAMAK","VURMAK","BEY??N","A??RIMAK","??AKAK","KEM??K");
        Tabuu tabuu379 = new Tabuu("ROMAT??ZM","JEST","SEVG??L??","M??M??K","ERKEK","BAYAN");
        Tabuu tabuu380 = new Tabuu("HAFR??YAT","??ANT??YE","B??NA","KATO","TOPRAK","KAZMAK");
        Tabuu tabuu381 = new Tabuu("MAHREM","BAYAN","ERKEK","G??NAH","A??IK","KADIN");
        Tabuu tabuu382 = new Tabuu("APSE","??LT??HAP","KEM??K","EZ??LMEK","D????","SIVI");
        Tabuu tabuu383 = new Tabuu("H??CRET","ARAP","D??N","??SLAM","MEKKE","KABE");
        Tabuu tabuu384 = new Tabuu("U??","0.5","??NCE","KIRTAS??YE","KALEM","0.7");
        Tabuu tabuu385 = new Tabuu("SE??ENEK","??STEK","BA??LI","OLMAK","YAPMAK","??IK");
        Tabuu tabuu386 = new Tabuu("KORNEA","G??RMEK","G??Z","G??ZL??K","LENS","TABAKA");
        Tabuu tabuu387 = new Tabuu("SPOR","KO??U","FITNESS","KAS","SA??LIK","MUTLULUK");
        Tabuu tabuu388 = new Tabuu("??TFA??YE","YANGIN","EK??P","SU","F????KIRTMAK","EV");
        Tabuu tabuu389 = new Tabuu("HALTER","A??IRLIK","NA??M S??LEYMANO??LU","ESK??","KAS","SPOR");
        Tabuu tabuu390 = new Tabuu("G??KY??Z??","SAT??RN","ASTRONOM??","MARS","D??NYA","UZAY");
        Tabuu tabuu391 = new Tabuu("HAC??VAT","KARAG??Z","GELENEK","T??YATRO","OYUN","CAH??L");
        Tabuu tabuu392 = new Tabuu("YASAK","OYUN","HAP??S","??IKMAK","CEZA","EV");
        Tabuu tabuu393 = new Tabuu("KABLO","BAKIR","TEL","??LETKEN","ELEKTRON??K","ELEKTR??K");
        Tabuu tabuu394 = new Tabuu("CESUR","BABAY??????T","KUVVETL??","KORKMAK","ATILGAN","D??VMEK");
        Tabuu tabuu395 = new Tabuu("KAPTAN","TAKIM","S??R??C??","KILAVUZ","GEM??","P??LOT");
        Tabuu tabuu396 = new Tabuu("??HRACAT","REKOR","T??CARET","??THALAT","DI??","??LKE");
        Tabuu tabuu397 = new Tabuu("KAMU","DEVLET","HALK","BA??KAN","MUHTAR","MAL");
        Tabuu tabuu398 = new Tabuu("BAMYA","YEMEK","SEBZE","YAPMAK","BES??N","YE????L");
        Tabuu tabuu399 = new Tabuu("B??BER","ACI","TATLI","??SOT","URFA","KIRMIZI");
        Tabuu tabuu400 = new Tabuu("ENSTRUMENTAL","??ALMAK","M??Z??K","G??TAR","??ALMAK","ALET");

        //300 tabuu kart?? i??in

        Tabuu tabuu401 = new Tabuu("C??HAZ","??R??N","MAK??NE","K??????K","ELEKTRON??K","APARAT");
        Tabuu tabuu402 = new Tabuu("??HT??YA??","VAR","ZORUNLU","YEMEK","SU","YA??AM");
        Tabuu tabuu403 = new Tabuu("ANTROPOMETR??","??L????","??NSAN","UZUNLUK","BOYUT","BEDEN");
        Tabuu tabuu404 = new Tabuu("REAKT??R","URANYUM","N??KLEER","RADYASYON","SANTRAL","??ERNOB??L");
        Tabuu tabuu405 = new Tabuu("PETROL","BENZ??N","YAKIT","MAZOT","AKARYAKIT","OTOMOB??L");
        Tabuu tabuu406 = new Tabuu("BETON","????MENTO","??N??AAT","GR??","DUVAR","YAPMAK");
        Tabuu tabuu407 = new Tabuu("??LET??????M","HABERLE??ME","TELEFON","MESAJ","TEKNOLOJ??","OPERAT??R");
        Tabuu tabuu408 = new Tabuu("TAM??R","ETMEK","ONARMAK","BOZULMAK","KIRILMAK","USTA");
        Tabuu tabuu409 = new Tabuu("G??VENL??K","KORUNMAK","TEHL??KE","????","YASA","G??REVL??");
        Tabuu tabuu410 = new Tabuu("JEOTERMAL","SU","BUHAR","SICAK","YER ALTI","MAGMA");
        Tabuu tabuu411 = new Tabuu("I??IK","ENERJ??","AMP??L","LAMBA","AYDINLATMAK","LED");
        Tabuu tabuu412 = new Tabuu("DALGA","DEN??Z","ENERJ??","OKYANUS","SU","S??RF");
        Tabuu tabuu413 = new Tabuu("ZEM??N","YER","M??MAR??","TEMEL","FAYANS","KAT");
        Tabuu tabuu414 = new Tabuu("??ATI","EV","KORUMAK","K??REM??T","KAT","??E????T");
        Tabuu tabuu415 = new Tabuu("DUVAR","BETON","YALITIM","PENCERE","TU??LA","BAH??E");
        Tabuu tabuu416 = new Tabuu("ARA??TIRMAK","??NTERNET","WIKIPEDIA","DEDEKT??F","BULMAK","SONU??");
        Tabuu tabuu417 = new Tabuu("K??MYASAL","TEPK??ME","FEN","ENERJ??","SIVI","DENEY");
        Tabuu tabuu418 = new Tabuu("D??N??????M","ENERJ??","SANTRAL","D??NMEK","YEN??","ELEKTR??K");
        Tabuu tabuu419 = new Tabuu("PAYLA??MAK","ORTAK","H??SSEDAR","YARDIM","FAK??R","ZENG??N");
        Tabuu tabuu420 = new Tabuu("TOPLUMSAL","??NSAN","KURAL","HALK","M??LLET","SORUN");
        Tabuu tabuu421 = new Tabuu("DAYANI??MA","B??RL??K","BERABERL??K","YARDIMLA??MA","DESTEK","ZORLUK");
        Tabuu tabuu422 = new Tabuu("SADAKA","YARDIM","PARA","??M??R","D??LENC??","FAK??R");
        Tabuu tabuu423 = new Tabuu("YARDIMLA??MA KURUMU","PARA","KIZILAY","YE????LAY","DO??AL AFET","MADD??/MANEV??");
        Tabuu tabuu424 = new Tabuu("SADAKA-?? CAR??YE","YARDIM","S??REKL??","??E??ME","HASTANE","OKUL");
        Tabuu tabuu425 = new Tabuu("TAVLA","ZAR","OYUN","YENMEK","MARS","KAPI");
        Tabuu tabuu426 = new Tabuu("OKEY","ZAR","ISTAKA","TA??","4 K??????","OYUN");
        Tabuu tabuu427 = new Tabuu("??ORAP","??NCE","KA??MAK","TEN RENG??","PAR??ZYEN","AYAK");
        Tabuu tabuu428 = new Tabuu("M??CEVHER","KADIN","TAKI","ALTIN","B??LEZ??K","KOLYE");
        Tabuu tabuu429 = new Tabuu("OJE","TIRNAK","RENK","S??RMEK","ASETON","KIRMIZI");
        Tabuu tabuu430 = new Tabuu("????M??EK","G??KY??Z??","YA??MUR","I??IK","KALP KR??Z??","BULUT");
        Tabuu tabuu431 = new Tabuu("STETESKOP","SES","KALP","DOKTOR","BOYUN","B??Y??TE??");
        Tabuu tabuu432 = new Tabuu("HEM????RE","HASTALIK","HASTANE","DOKTOR","??LK YARDIM","SA??LIK");
        Tabuu tabuu433 = new Tabuu("MECL??S H??K??MET??","M??LLETVEK??L??","TBMM","Y??NETMEN","Y??NET??M","??RADE");
        Tabuu tabuu434 = new Tabuu("??ALI??MAK","ANNE","BABA","????","PARA","DERS");
        Tabuu tabuu435 = new Tabuu("SARILMAK","ANNE","BABA","SEVG??","??OCUK","A??MAK");
        Tabuu tabuu436 = new Tabuu("MAYIS","ANNE","??OCUK","KUTLAMA","D??NYA","AY");
        Tabuu tabuu437 = new Tabuu("ANNELER G??N??","??????EK","HED??YE","??OCUK","TEBR??K","SARILMAK");
        Tabuu tabuu438 = new Tabuu("GEN","G??REV B??R??M??","DNA","??ZELL??K","N??KLEOT??D","????FRE");
        Tabuu tabuu439 = new Tabuu("C??NS??YET","KIZ","ERKEK","X KROMOZOMU","Y KROMOZOMU","BABA");
        Tabuu tabuu440 = new Tabuu("SINAV","DERS","PUAN","OKUL","SINIF","G??RMEK");
        Tabuu tabuu441 = new Tabuu("R??T??M","M??Z??K","TEMPO","TASARIM","KONU","RES??M");
        Tabuu tabuu442 = new Tabuu("SES","TEL","ENERJ??","M??Z??K","??IKMAK","KONU??MAK");
        Tabuu tabuu443 = new Tabuu("GRAF??K","G??RSEL","TASARIM","KONU","PASTA","????Z??M");
        Tabuu tabuu444 = new Tabuu("??EVRE","K??RL??L??K","DO??A","D??NYA","????P","ETRAF");
        Tabuu tabuu445 = new Tabuu("KARBON","ATOM","MONOKS??T","ZARAR","FABR??KA","BACA");
        Tabuu tabuu446 = new Tabuu("??KL??M","DO??A","??EVRE","MEVS??M","DE????????KL??K","B??LGE");
        Tabuu tabuu447 = new Tabuu("TEMEL","M??MAR??","??N??AAT","EV","TA??IYICI","ELEMAN");
        Tabuu tabuu448 = new Tabuu("F??SYON","URANYUM","ATOM","TEPK??ME","RADYASYON","PAR??ALANMA");
        Tabuu tabuu449 = new Tabuu("ARA??","MALZEME","GERE??","????","DERS","KULLANMAK");
        Tabuu tabuu450 = new Tabuu("??CAT","YEN??L??K","MUC??T","BULMAK","OLMAYAN","??IKARMAK");
        Tabuu tabuu451 = new Tabuu("PANEL","KONFERANS","G??NE??","ENERJ??","ELEKTR??K","YEN??LENEB??L??R");
        Tabuu tabuu452 = new Tabuu("SERG??","D??NEM","OKUL","FUAR","SUNMAK","DERS");
        Tabuu tabuu453 = new Tabuu("??RET??M","FABR??KA","EL","MAK??NE","SANAY??","SER??");
        Tabuu tabuu454 = new Tabuu("REKLAM","TELEV??ZYON","SATI??","PARA","YAPMAK","UYGULAMA");
        Tabuu tabuu455 = new Tabuu("??NOVASYON","YEN??L??K","YEN??LE????M","TASARIM","??R??N","D??????NME");
        Tabuu tabuu456 = new Tabuu("B??L??M","??NSAN","ARA??TIRMA","DENEY","LABORATUVAR","YEN??");
        Tabuu tabuu457 = new Tabuu("MUTLAK DE??ER","POZ??T??F","EKS??","SAYI","UZAKLIK","MATEMAT??K");
        Tabuu tabuu458 = new Tabuu("OR??J??N","SIFIR","NOKTA","X E????TT??R Y","KOORD??NAT","MATEMAT??K");
        Tabuu tabuu459 = new Tabuu("FA??Z","HARAM","OLMAK","BANKA","AYLIK","ATM");
        Tabuu tabuu460 = new Tabuu("ENFLASYON","EKONOM??","ARTI??","MAL","??R??N","??LKE");
        Tabuu tabuu461 = new Tabuu("YARI ??AP","??EMBER","R","DA??RE","MERKEZ","??EVRE");
        Tabuu tabuu462 = new Tabuu("ZARAR","KAR","ALI??","SATI??","MAL","Y??ZDE");
        Tabuu tabuu463 = new Tabuu("ROBOT","KEND??","MAK??NE","YAPAY ZEKA","FABR??KA","SOPH??A");
        Tabuu tabuu464 = new Tabuu("HAYAT","YA??AM","??NSAN","??M??R","ZAMAN","G??ZEL");
        Tabuu tabuu465 = new Tabuu("MAK??NE","??RET??M","KAS G??C??","HIZLI","KOLAY","ELEKTR??K");
        Tabuu tabuu466 = new Tabuu("TUHAF??YE","ELB??SE","OKUL","G??YS??","KUMA??","ETEK");
        Tabuu tabuu467 = new Tabuu("T??B??TAK","SERG??","FUAR","B??L??M","??CAT","YAPMAK");
        Tabuu tabuu468 = new Tabuu("PROJE","NOT","PUAN","KONTROL","??RET??M","TASARIM");
        Tabuu tabuu469 = new Tabuu("AT??LYE","DERS","TASARIM","TEKNOLOJ??","????","B??LG??");
        Tabuu tabuu470 = new Tabuu("CETVEL","??L????M","MATEMAT??K","UZUNLUK","BOY","????Z??M");
        Tabuu tabuu471 = new Tabuu("MAKAS","KA??IT","KESMEK","KUMA??","TASARIM","ARA??");
        Tabuu tabuu472 = new Tabuu("PAZARLAMA","SATI??","PARA","??R??N","M????TER??","MAL");
        Tabuu tabuu473 = new Tabuu("H??DRO ELEKTR??K SANTRAL??","ENERJ??","BARAJ","ATAT??RK","SU","T??RB??N");
        Tabuu tabuu474 = new Tabuu("ED??SON","AMP??L","ELEKTR??K","EINSTEIN","NIKOLA","I??IK");
        Tabuu tabuu475 = new Tabuu("MOTOR","P??STON","YAKIT","ARA??","TA??IT","B??S??KLET");
        Tabuu tabuu476 = new Tabuu("ERGONOM??","RAHAT","G??VENL??","SA??LIKLI","ANTROPOMETR??","OTURMAK");
        Tabuu tabuu477 = new Tabuu("TASARIM","DI?? G??R??N??M","F??K??R","KURGU","RENK","??R??N");
        Tabuu tabuu478 = new Tabuu("TEKNOLOJ??","??R??N","B??LG??SAYAR","??RETMEK","KOLAY","YEN??");
        Tabuu tabuu479 = new Tabuu("BULU??","??CAT","B??L??M","??NSAN","YEN??","Y??ZYIL");
        Tabuu tabuu480 = new Tabuu("YALITIM","SO??UK","SICAK","TERMOS","DUVAR","CEPHE");
        Tabuu tabuu481 = new Tabuu("KULLANI??LI","??R??N","PRAT??K","K??????K","TASARIM","EV");
        Tabuu tabuu482 = new Tabuu("UYGUN","G??RE","TAM","??L????M","EVRE","G??YMEK");
        Tabuu tabuu483 = new Tabuu("B??????M","TASARIM","ARA??","????Z??M","KOMPOZ??SYON","TEMEL");
        Tabuu tabuu484 = new Tabuu("G??LGE","G??NE??","I??IK","YANSIMA","KARANLIK","S??YAH");
        Tabuu tabuu485 = new Tabuu("B??YOK??TLE","ENERJ??","YA??","GER?? D??N??????M","YEN??LENEB??L??R","SIVI");
        Tabuu tabuu486 = new Tabuu("YAPI","M??MAR??","??N??AAT","TASARIM","YAPMAK","KONUT");
        Tabuu tabuu487 = new Tabuu("KOMPOZ??SYON","YAZMAK","????Z??M","DERS","ANLATMAK","SONU??");
        Tabuu tabuu488 = new Tabuu("ESTET??K","G??ZEL","??IK","TARZ","YAPTIRMAK","??ZELL??K");
        Tabuu tabuu489 = new Tabuu("DEFTERDAR","D??VAN","MAL??YE","ANADOLU","RUMEL??","MAL");
        Tabuu tabuu490 = new Tabuu("MATBAA","28 MEHMET","M??TEFERR??KA","K??TAP","LALE DEVR??","FOTOKOP??");
        Tabuu tabuu491 = new Tabuu("P??R?? RE??S","HAR??TA","DEN??Z","OSMANLI","DONANMA","??MPARATORLUK");
        Tabuu tabuu492 = new Tabuu("LALE DEVR??","PATRONA HAL??L","??SYAN","LALE","??????EK","3. AHMET");
        Tabuu tabuu493 = new Tabuu("??AH?? TOPU","1453","??STANBUL","FAT??H","B??ZANS","TOPKAPI");
        Tabuu tabuu494 = new Tabuu("AYASOFYA","A??MAK","AK PART??","??BADET","YUNAN","AVRUPA");
        Tabuu tabuu495 = new Tabuu("CAM","SODA","TOPRAK","PENCERE","FEN??KEL??LER","SAYDAM");
        Tabuu tabuu496 = new Tabuu("N??FUS","??NSAN","G????","SATIM","MEVCUT","RAKIM");
        Tabuu tabuu497 = new Tabuu("SEYAHATNAME","EVL??YA ??ELEB??","GEZ??","TAT??L","YAZAR","SEYEHAT");
        Tabuu tabuu498 = new Tabuu("R??NESANS","B??L??M","GALILEO","YEN??DEN","DO??U??","AVRUPA");
        Tabuu tabuu499 = new Tabuu("MONA L??SA","TABLO","DA V??NC??","??TALYA","R??NESANS","AVRUPA");
        Tabuu tabuu500 = new Tabuu("G??NE?? TAKV??M??","G??NE??","MISIR","M??LAD??","ZAMAN","YIL");
        Tabuu tabuu501 = new Tabuu("N??FUS","SAYIM","2. MAHMUT","ASKER","VERG??","DEVLET");
        Tabuu tabuu502 = new Tabuu("PAP??R??S","B??TK??","YAZI","KA??IT","MISIR","YAPRAK");
        Tabuu tabuu503 = new Tabuu("KIRKPINAR","G??NE??","SPOR","ER MEYDANI","PEHL??VAN","AY");
        Tabuu tabuu504 = new Tabuu("??CAT","BULU??","GEL????T??RMEK","B??L??M ADAMI","EDISON","BULMAK");
        Tabuu tabuu505 = new Tabuu("????MPE KALES??","AVRUPA","B??ZANS","OSMANLI","OSMAN BEY","ORHAN BEY");
        Tabuu tabuu506 = new Tabuu("Y??R??NGE","D??NYA","UZAY GEM??S??","G??RMEK","YOL","TAK??P ETMEK");
        Tabuu tabuu507 = new Tabuu("DOKUZ CANLI","??LMEK","D????MEK","DAYANMAK","CESUR","??L??MS??Z");
        Tabuu tabuu508 = new Tabuu("TABURCU OLMAK","??Y??LE??MEK","HASTA","CANLI","YATMAK","HASTAHANE");
        Tabuu tabuu509 = new Tabuu("NEF??S","??RADE","??STEMEK","G??DERMEK","YEMEK","YATI??TIRMAK");
        Tabuu tabuu510 = new Tabuu("KUZUKULA??I","SALATA","EK????","OT","YAPRAK","ROKA");
        Tabuu tabuu511 = new Tabuu("U??ARI","KA??ARI","BA??LI","SORUMLULUK","E??LENMEK","GEZMEK");
        Tabuu tabuu512 = new Tabuu("A??DALI","A??IR","TATLI","D??L","KARMA??IK","??ERBET");
        Tabuu tabuu513 = new Tabuu("??ALAKALEM","YAZMAK","??ZENT??","BA??TAN SAVMAK","NOT","ACELE");
        Tabuu tabuu514 = new Tabuu("ANT??KA","E??YA","ESK??","TAR??H","EV","KLAS??K");
        Tabuu tabuu515 = new Tabuu("C??NG??Z","AKILLI","ZEK??","UYANIK","KURNAZ","??IKARI OLMAK");
        Tabuu tabuu516 = new Tabuu("AMB??YANS","ORTAM","HAVA","G??ZEL","MEKAN","MAL??KANE");
        Tabuu tabuu517 = new Tabuu("HENTBOL","SPOR","TOP","EL","D??REK","TAKIM");
        Tabuu tabuu518 = new Tabuu("KURU ??FT??RA","ATMAK","SAKLAMAK","YALAN","GER??EK","SU??LAMAK");
        Tabuu tabuu519 = new Tabuu("TROMPET","M??Z??K","BANDO","NEFESL??","??FLEMEK","SAKSAFON");
        Tabuu tabuu520 = new Tabuu("G??NE?? BANYOSU","YANMAK","KREM","YATMAK","BRONZ","KUM");
        Tabuu tabuu521 = new Tabuu("??ARLATAN","??AKLABAN","DOLANDIRMAK","??VMEK","E??LEND??RMEK","SOYTARI");
        Tabuu tabuu522 = new Tabuu("ANA KUZUSU","MUHALLEB??","ANNE","BABA","B??Y??TMEK","BA??IRMAK");
        Tabuu tabuu523 = new Tabuu("ADRENAL??N","HORMON","TRAF??K","??FKE","HEYECAN","KO??MAK");
        Tabuu tabuu524 = new Tabuu("ORTAPED??K","RAHAT","SA??LIK","TABAN","??ZEL","AYAKKABI");
        Tabuu tabuu525 = new Tabuu("??ORBA","SU","YEMEK","TOZ","SIVI","DOMATES");
        Tabuu tabuu526 = new Tabuu("C??SSE","KALIP","??R??","YAPI","GEN????","K??LO");
        Tabuu tabuu527 = new Tabuu("KENAN SOFUO??LU","YARI??MA","M??LLETVEK??L??","MOTOR","??AMP??YON","P??ST");
        Tabuu tabuu528 = new Tabuu("MUAF TUTMAK","VERG??","BULUNMAK","DEVLET","??DEMEK","G??REV");
        Tabuu tabuu529 = new Tabuu("AKINTI","BURUN","BO??AZ","Y??ZMEK","K??REK ??EKMEK","GEN??Z");
        Tabuu tabuu530 = new Tabuu("YARIM YAMALAK","BA??TAN SAVMA","??ZENS??Z","YAPMAK","EKS??K","B??TMEK");
        Tabuu tabuu531 = new Tabuu("A??A??KAKAN","KU??","GAGALAMAK","ORMAN","VURMAK","OYMAK");
        Tabuu tabuu532 = new Tabuu("DEFNE YAPRA??I","A??A??","BALIK","KOKMAK","SABUN","YEMEK");
        Tabuu tabuu533 = new Tabuu("ZEVZEK","BO?? KONU??MAK","SA??MA SAPAN","GEVEZE","U??RA??MAK","??ENES?? D??????K");
        Tabuu tabuu534 = new Tabuu("??DRAK ETMEK","ANLAMAK","AKIL ERMEK","KAVRAMAK","AKLI BA??INA GELMEK","ALGILAMAK");
        Tabuu tabuu535 = new Tabuu("??ZDE??LE??MEK","SEVMEK","AYNI","E????T","BENZER","AYRILMAK");
        Tabuu tabuu536 = new Tabuu("??LT??MAS","HAKSIZ","GE??MEK","KAYIRMAK","TORP??L","AYRICALIK");
        Tabuu tabuu537 = new Tabuu("AHENK","UYUM","SES","DANS","??????R","UZLA??MA");
        Tabuu tabuu538 = new Tabuu("J??PON","ETEK","ELB??SE","G??YMEK","KISA","????");
        Tabuu tabuu539 = new Tabuu("CAZ??BE","??EK??C??","ALIMLI","KADIN","G??ZELL??K","ZAR??F");
        Tabuu tabuu540 = new Tabuu("OYALAMAK","KONU??MAK","ERTELEMEK","GEC??KMEK","LAFA TUTMAK","GEREK");
        Tabuu tabuu541 = new Tabuu("ASMA KAT","TAVAN","MERD??VEN","D??KKAN","??ST","B??R??NC??");
        Tabuu tabuu542 = new Tabuu("KUVER","EKMEK","RESTORAN","LOKANTA","HESAP","??CRET");
        Tabuu tabuu543 = new Tabuu("T??MSAH G??ZYA??I","??Z??LMEK","A??LAMAK","YALAN","??NANMAK","GER??EK");
        Tabuu tabuu544 = new Tabuu("M??TEMAD??YEN","ZAMAN","ARA VERMEK","HEP","S??RMEK","SIKLIK");
        Tabuu tabuu545 = new Tabuu("SIKBO??AZ","ZORLAMAK","SIKI??TIRMAK","SORMAK","BASKI YAPMAK","??STEMEK");
        Tabuu tabuu546 = new Tabuu("KAMU SPOTU","B??LG??","TELEV??ZYON","REKLAM","DEVLET","YARDIM");
        Tabuu tabuu547 = new Tabuu("TURNUSOL KA??IDI","AS??T","BAZ","AYIRMAK","LABORATUVAR","ANLAMAK");
        Tabuu tabuu548 = new Tabuu("SEFER TASI","????LE YEME????","TA??IMAK","OKUL","KAP","BESLENME");
        Tabuu tabuu549 = new Tabuu("M??NDER","OTURMAK","YASLANMAK","ZEM??N","RAHATLIK","YASTIK");
        Tabuu tabuu550 = new Tabuu("MUKAYESE","KAR??ILA??TIRMAK","BENZETME","ARASINDA","KIYAS","??OCUK");
        Tabuu tabuu551 = new Tabuu("ERZAK","YEMEK","SEBZE","MEYVE","??HT??YA??","DEPO");
        Tabuu tabuu552 = new Tabuu("UYKULUK","SAKATAT","ET","KOKORE??","S??TL??CE","KUZU");
        Tabuu tabuu553 = new Tabuu("YEM TORBASI","BA??LAMAK","AT","ACIKMAK","TAKMAK","BA??");
        Tabuu tabuu554 = new Tabuu("BA??DA?? KURMAK","BACAK","YOGA","OTURMAK","YER","AYAK");
        Tabuu tabuu555 = new Tabuu("GEL??N ADAYI","EVLENMEK","A??LE","D??????N","DAMAT","KONVOY");
        Tabuu tabuu556 = new Tabuu("??EMK??RMEK","KONU??MAK","BA??IRMAK","KAR??I GELMEK","CEVAP VERMEK","????RKEFLE??MEK");
        Tabuu tabuu557 = new Tabuu("DALLI BUDAKLI","KARI??IK","DERT","A??A??","??ETREF??L","B??Y??MEK");
        Tabuu tabuu558 = new Tabuu("DEVRETMEK","AKTARMAK","D??KKAN","??KRAM??YE","P??YANGO","LOTO");
        Tabuu tabuu559 = new Tabuu("F??TURSUZ","??EK??NMEK","UMURSAMAZ","DAVRANMAK","RAHATLIK","KAYGI");
        Tabuu tabuu560 = new Tabuu("T??P","OCAK","GAZ","??AY","PATLAMAK","MUTFAK");
        Tabuu tabuu561 = new Tabuu("P??KN??K T??P??","YEMEK P??????RMEK","??AY YAPMAK","K??????K","GAZ","TA??IMAK");
        Tabuu tabuu562 = new Tabuu("AYASOFYA","CAM??","K??L??SE","TUR??ST","SULTANAHMET","TOPKAPI SARAYI");
        Tabuu tabuu563 = new Tabuu("TUVAL","RES??M","SULU BOYA","KARA KALEM","RESSAM","DA VINCI");
        Tabuu tabuu564 = new Tabuu("YUVALAMA","NOHUT","K??FTE","GAZ??ANTEP","??ORBA","ANALI KIZLI");
        Tabuu tabuu565 = new Tabuu("BOLLYWOOD","S??NEMA","H??ND??STAN","F??LM","DANS","ESMER");
        Tabuu tabuu566 = new Tabuu("HOLLYWOOD","S??NEMA","AMER??KAN","F??LM","AKS??YON","KAMERA");
        Tabuu tabuu567 = new Tabuu("M??NNET","G??N??L","BOR??","??Y??L??K","YARDIM","DUYMAK");
        Tabuu tabuu568 = new Tabuu("BERL??N","ALMANYA","BA??KENT","DUVAR","FEST??VAL","D??NYA");
        Tabuu tabuu569 = new Tabuu("ETOBUR","KU??","ET","B??TK??","HAYVAN","CANLI");
        Tabuu tabuu570 = new Tabuu("????KA??IT??I","SAHTEKAR","DOLANDIRICI","YALAN","D??ZENBAZ","KANDIRMAK");
        Tabuu tabuu571 = new Tabuu("ZIMBA","BASMAK","ATA??","TEL","MAK??NA","AL??M??NYUM");
        Tabuu tabuu572 = new Tabuu("ZUMBA","DANS","AEROB??K","LAT??N","GRUP","SPOR SALONU");
        Tabuu tabuu573 = new Tabuu("ABANMAK","DAYANMAK","TOP","Y??KLENMEK","??ULLANMAK","A??IRLIK");
        Tabuu tabuu574 = new Tabuu("TAL??P","EVLENMEK","??ZD??VA?? PROGRAMI","ADAY","KIZ","SEVMEK");
        Tabuu tabuu575 = new Tabuu("KULAK M??SAF??R??","KONU??MAK","D??NLEMEK","ANLATMAK","DUYMAK","DED??KODU");
        Tabuu tabuu576 = new Tabuu("DED??KODU","G??NAH","KULAK M??SAF??R??","BA??KASI","ARKADAN KONU??MAK","D??N");
        Tabuu tabuu577 = new Tabuu("SEMPOZYUM","SEM??NER","TOPLANTI","KONU??MAK","B??LG??","D??ZENLEMEK");
        Tabuu tabuu578 = new Tabuu("AMA??","HEDEF","YAPILACAK","HAYAL","ED??NMEK","??ALI??MAK");
        Tabuu tabuu579 = new Tabuu("MIZIKA","M??Z??K","ALET","ENSTR??MAN","DUDAK","??FLEMEK");
        Tabuu tabuu580 = new Tabuu("PALDIR K??LD??R","GELMEK","DAVETS??Z","M??SAF??R","HIZLI","AN??DEN");
        Tabuu tabuu581 = new Tabuu("ANA??","??EFKAT","SEVG??","??LG??","??OCUK","BAKMAK");
        Tabuu tabuu582 = new Tabuu("Y??ZLE??T??RMEK","KAR??I KAR??IYA","YALAN","KONU??MAK","SIKI??TIRMAK","TARAF");
        Tabuu tabuu583 = new Tabuu("TAROT","FAL","BAKMAK","GELECEK","G??RMEK","KAHVE");
        Tabuu tabuu584 = new Tabuu("KA??M??R","HALI","Y??N","??PEK","??RMEK","OTURMAK");
        Tabuu tabuu585 = new Tabuu("KUMA??","ETEK","G??YS??","TERZ??","Y??N","??PEK");
        Tabuu tabuu586 = new Tabuu("S??ND??R??M S??STEM??","M??DE","BA??IRSAK","V??CUT","YEMEK","ORGAN");
        Tabuu tabuu587 = new Tabuu("KA??AK??I","HIRSIZ","KAPKA??","UYU??TURUCU","SATMAK","YASA DI??I");
        Tabuu tabuu588 = new Tabuu("YUFKA Y??REK","YUMU??AK","KALP","??Y??","MERHAMET","??Z??LMEK");
        Tabuu tabuu589 = new Tabuu("BARF??KS","??UBUK","PARALEL","J??MNAST??K","??EKMEK","ASILMAK");
        Tabuu tabuu590 = new Tabuu("EZBER BOZMAK","DE??????T??RMEK","YANLI??","AKIL","G??STERMEK","TUTMAK");
        Tabuu tabuu591 = new Tabuu("S??NERJ??","YARATMAK","ARASINDA","ORTAK","G????","??STEK");
        Tabuu tabuu592 = new Tabuu("??EYTANA UYMAK","K??T??","HAP??S","SU??","??FKE","G??NAH");
        Tabuu tabuu593 = new Tabuu("M??SVEDDE","KARALAMAK","NOT ALMAK","YAZMAK","KA??IT","TEM??Z");
        Tabuu tabuu594 = new Tabuu("SALAMURA","ZEYT??N","TUZ","SU","BALIK","YATIRMAK");
        Tabuu tabuu595 = new Tabuu("NAZLANMAK","??IMARMAK","NAZLI","??STEKS??Z","BEKLEMEK","SEVG??L??");
        Tabuu tabuu596 = new Tabuu("DAMITMAK","DONATMAK","KAYNATMAK","SU","SAF","K??MYA");
        Tabuu tabuu597 = new Tabuu("DAYALI D????EL??","EV","MOB??LYA","E??YA","K??RALIK","SATILIK");
        Tabuu tabuu598 = new Tabuu("??SOT","KIRMIZI","B??BER","ACI","BAHARAT","URFA");
        Tabuu tabuu599 = new Tabuu("ANEKDOT","OLAY","H??KAYE","ANLATMAK","??YK??","??LG??N??");
        Tabuu tabuu600 = new Tabuu("TABU KIRMAK","ZORLUK","G????","BELA","BA??ARMAK","HEDEF");
        Tabuu tabuu601 = new Tabuu("DE????RMEN","UN","EKMEK","PERVANE","R??ZGAR","TEPE");
        Tabuu tabuu602 = new Tabuu("AFALLAMAK","??A??IRMAK","AN??","BEKLENMED??K","S??RPR??Z","??OK");
        Tabuu tabuu603 = new Tabuu("DO??A??LAMA","M??Z??K","EZBERLEMEK","??ALMAK","??ALI??MAK","T??YATRO");
        Tabuu tabuu604 = new Tabuu("K??P??RTMEK","AYRAN","BALONCUK","KAHVE","SODA","M??KSER");
        Tabuu tabuu605 = new Tabuu("REPL??K","F??LM","S??YLEMEK","T??YATRO","KONU??MAK","EZBER");
        Tabuu tabuu606 = new Tabuu("KARAKALEM","????ZMEK","S??YAH","BOYA","P??LOT","RES??M");
        Tabuu tabuu607 = new Tabuu("Z??F??R?? KARANLIK","GECE","KARANLIK","I??IK","G??RMEK","LAMBA");
        Tabuu tabuu608 = new Tabuu("????ZMEY?? A??MAK","??LER?? G??TMEK","A??MAK","SINIR","B??LMEK","????ZG??");
        Tabuu tabuu609 = new Tabuu("BO??BO??AZ","KONU??MAK","GEVEZE","SIR","S??YLEMEK","ANLATMAK");
        Tabuu tabuu610 = new Tabuu("??ADE??Z??YARET","G??R????MEK","GER??","GELMEK","G??TMEK","EV");





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







        //MA??AZA'DA SATIN ALIMLAR ??????N KULLANILACAK

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


            //Ma??aza i??in
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

        //shared preferences kontrol??n?? burada yap e??er 1 gelirse yani kullan??c?? paketi sat??n ald??ysa if de true sa??lanm???? olacak ve tabu kartlar?? kullan??c??n??n hesab??na y??klenmi?? olacak.

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
        builder.setMessage("Oyundan ????kmak istedi??inize emin misiniz ?");
        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sonradan yazd??????m kodlar 14/01/2021
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
                        Toast.makeText(ClassicPlayActivity.this,"L??tfen sat??n alma i??leminizi oyuna ??ye olarak ger??ekle??tiriniz",Toast.LENGTH_LONG).show();
                    }


                }

            }
        }

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassicPlayActivity.this);
            builder.setTitle("SATIN ALMA ????LEM??");
            builder.setMessage("Sat??n alma i??lemi iptal edildi");
            builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();
        }


    }
}
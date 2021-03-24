package com.furkanayaz.anlatbakalimcizbakalim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd interstitialAdClassicPlay,interstitialAdDrawingPlay,interstitialAdSettings,interstitialAdShop;
    private CardView cardViewClassicPlay,cardViewDrawingPlay,cardViewSettings,cardViewShop,cardViewLogin,cardViewSignUp;
    private CardView cardViewMenuScore,cardViewMenuScore1,cardViewMenuScore2,cardViewMenuScore3;
    private TabLayout tabLayout2;
    private ViewPager2 viewPager2;
    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<String> fragmentTitleArrayList;
    private TextInputEditText textInputEditTextClassicTeamA,textInputEditTextClassicTeamB;
    private TextInputEditText textInputEditTextDrawingTeamA,textInputEditTextDrawingTeamB;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SharedPreferences sharedPreferencesPurchase;
    private SharedPreferences.Editor editorPurchase;

    private ProgressBar progressBar2;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUserController;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseAuth firebaseAuthPurchases;
    private FirebaseUser firebaseUserPurchases;
    private FirebaseDatabase databasePurchases;
    private DatabaseReference databaseReferencePurchases;

    //Login Activity's Visual Objects
    private TextInputEditText inputEditTextLoginEmail,inputEditTextLoginPassword;
    private TextView textViewForgotPassword;
    private TextView textViewShowScores1,textViewShowScores2,textViewShowScores3;

    //Forgot Password inside Login Activity
    private TextInputEditText textInputEditTextForgotPassword;


    //Sign Up Activity's Visual Objects
    private TextInputEditText inputEditTextSignName,inputEditTextSignLastName,inputEditTextSignEmail,inputEditTextSignPassword;


    private int showScoreCounter = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardViewClassicPlay = findViewById(R.id.cardViewClassicPlay);
        cardViewDrawingPlay = findViewById(R.id.cardViewDrawingPlay);
        cardViewSettings = findViewById(R.id.cardViewSettings);
        cardViewShop = findViewById(R.id.cardViewShop);
        cardViewLogin = findViewById(R.id.cardViewLogin);
        cardViewSignUp = findViewById(R.id.cardViewSignUp);

        cardViewMenuScore = findViewById(R.id.cardViewMenuScore);
        cardViewMenuScore1 = findViewById(R.id.cardViewMenuScore1);
        cardViewMenuScore2 = findViewById(R.id.cardViewMenuScore2);
        cardViewMenuScore3 = findViewById(R.id.cardViewMenuScore3);

        progressBar2 = findViewById(R.id.progressBar2);

        tabLayout2 = findViewById(R.id.tabLayout2);
        viewPager2 = findViewById(R.id.viewPager2);



        sharedPreferences = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
        boolean buyprocess = sharedPreferences.getBoolean("buynoads",false);

        if (!buyprocess){
            MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });


            interstitialAdLoader();

        }





        fragmentArrayList = new ArrayList<>();
        fragmentTitleArrayList = new ArrayList<>();

        firebaseUserController = firebaseAuth.getCurrentUser();

        if (firebaseUserController != null){
            cardViewLogin.setVisibility(View.INVISIBLE);
            cardViewSignUp.setVisibility(View.INVISIBLE);
            tabLayout2.setVisibility(View.VISIBLE);
            viewPager2.setVisibility(View.VISIBLE);
            //database'den kullanıcı bilgilerini textview'e yazdır.
        }else {
            cardViewLogin.setVisibility(View.VISIBLE);
            cardViewSignUp.setVisibility(View.VISIBLE);
            tabLayout2.setVisibility(View.INVISIBLE);
            viewPager2.setVisibility(View.INVISIBLE);
        }




        fragmentTitleArrayList.add("PROFIL");
        //fragmentTitleArrayList.add("PROFIL AYARLARI");

        fragmentArrayList.add(new FragmentProfile());
        //fragmentArrayList.add(new FragmentProfileSettings());


        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(MainActivity.this);
        viewPager2.setAdapter(myViewPagerAdapter);

        new TabLayoutMediator(tabLayout2,viewPager2,(tab, position) -> tab.setText(fragmentTitleArrayList.get(position))).attach();

        cardViewClassicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!buyprocess){
                    if (interstitialAdClassicPlay.isLoaded()){
                        interstitialAdClassicPlay.show();
                    }
                }

                showAlertDialogForClassic();

            }
        });

        if (!buyprocess){
            interstitialAdListener();
        }

        cardViewDrawingPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!buyprocess){
                    if (interstitialAdDrawingPlay.isLoaded()){
                        interstitialAdDrawingPlay.show();
                    }
                }



                showAlertDialogForDrawing();

            }
        });

        if (!buyprocess){
            interstitialAdListener();
        }

        cardViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!buyprocess){
                    if (interstitialAdSettings.isLoaded()){
                        interstitialAdSettings.show();
                    }
                }

                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);

            }
        });

        if (!buyprocess){
            interstitialAdListener();
        }

        cardViewShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!buyprocess){
                    if (interstitialAdShop.isLoaded()){
                        interstitialAdShop.show();
                    }
                }

                Intent intent = new Intent(MainActivity.this,ShopActivity.class);
                startActivity(intent);

            }
        });

        cardViewMenuScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (showScoreCounter%2){
                    case 0:
                        cardViewMenuScore1.setVisibility(View.VISIBLE);
                        cardViewMenuScore2.setVisibility(View.VISIBLE);
                        cardViewMenuScore3.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        cardViewMenuScore1.setVisibility(View.INVISIBLE);
                        cardViewMenuScore2.setVisibility(View.INVISIBLE);
                        cardViewMenuScore3.setVisibility(View.INVISIBLE);
                        break;
                }
                showScoreCounter++;
            }
        });

        if (!buyprocess){
            interstitialAdListener();
        }



        cardViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogForLogin();

            }
        });


        cardViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogForSignUp();



            }
        });


    }

    private void interstitialAdListener() {
        interstitialAdClassicPlay.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                interstitialAdClassicPlay.loadAd(new AdRequest.Builder().build());
                interstitialAdDrawingPlay.loadAd(new AdRequest.Builder().build());
                interstitialAdSettings.loadAd(new AdRequest.Builder().build());
                interstitialAdShop.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void interstitialAdLoader() {
        interstitialAdClassicPlay = new InterstitialAd(MainActivity.this);
        interstitialAdDrawingPlay = new InterstitialAd(MainActivity.this);
        interstitialAdSettings = new InterstitialAd(MainActivity.this);
        interstitialAdShop = new InterstitialAd(MainActivity.this);

        interstitialAdClassicPlay.setAdUnitId("ca-app-pub-5793841848623320/7778978552");
        interstitialAdClassicPlay.loadAd(new AdRequest.Builder().build());

        interstitialAdDrawingPlay.setAdUnitId("ca-app-pub-5793841848623320/3293756872");
        interstitialAdDrawingPlay.loadAd(new AdRequest.Builder().build());

        interstitialAdSettings.setAdUnitId("ca-app-pub-5793841848623320/5728348524");
        interstitialAdSettings.loadAd(new AdRequest.Builder().build());

        interstitialAdShop.setAdUnitId("ca-app-pub-5793841848623320/6849858508");
        interstitialAdShop.loadAd(new AdRequest.Builder().build());
    }


    private void showAlertDialogForLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("ANLAT BAKALIM'A GİRİŞ YAP");
        View view = getLayoutInflater().inflate(R.layout.alert_login_design,null);

        inputEditTextLoginEmail = view.findViewById(R.id.inputEditTextLoginEmail);
        inputEditTextLoginPassword = view.findViewById(R.id.inputEditTextLoginPassword);
        textViewForgotPassword = view.findViewById(R.id.textViewForgotPassword);



        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Anlat Bakalim Şifremi Unuttum");
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_forgotpassword_design,null);
                textInputEditTextForgotPassword = view1.findViewById(R.id.textInputEditTextForgotPassword);
                builder1.setView(view1);
                builder1.setPositiveButton("GÖNDER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = textInputEditTextForgotPassword.getText().toString().trim();

                        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            Toast.makeText(MainActivity.this,"Lütfen bilgilerinizi kontrol ediniz",Toast.LENGTH_SHORT).show();
                        }else {
                            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this,"Şifre değişikliği bağlantısı hesabınıza gönderildi",Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(MainActivity.this,"Bağlantı adresi hesabınıza gönderilemedi. Bu e-mail adresine kayıtlı hesap bulunmayabilir.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }



                    }
                });

                builder1.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder1.create().show();

            }
        });



        builder.setView(view);

        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("GİRİŞ YAP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = inputEditTextLoginEmail.getText().toString().trim();
                String password = inputEditTextLoginPassword.getText().toString();

                cardViewLogin.setVisibility(View.INVISIBLE);
                cardViewSignUp.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                tabLayout2.setVisibility(View.INVISIBLE);
                viewPager2.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!email.isEmpty() && !password.isEmpty() && !Patterns.EMAIL_ADDRESS.pattern().matches(email)){
                            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("CommitPrefEdits")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()){
                                        firebaseAuthPurchases = FirebaseAuth.getInstance();
                                        firebaseUserPurchases = firebaseAuthPurchases.getCurrentUser();

                                        if (firebaseUserPurchases != null){
                                            databasePurchases = FirebaseDatabase.getInstance();
                                            databaseReferencePurchases = databasePurchases.getReference("purchases50").child(firebaseUserPurchases.getUid());

                                            sharedPreferencesPurchase = getSharedPreferences("purchaseprocess",MODE_PRIVATE);
                                            editorPurchase = sharedPreferencesPurchase.edit();

                                            editorPurchase.clear();

                                            databaseReferencePurchases.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot d:snapshot.getChildren()){
                                                        if (d.child("purchase").getValue().toString().equals(firebaseUserPurchases.getUid())){
                                                            editorPurchase.putBoolean("buy50drawingtabuu",true);
                                                            Log.e("Shared log: ","saving50");
                                                            editorPurchase.commit();

                                                            //databasePurchases.getReference("");
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReferencePurchases = databasePurchases.getReference("purchases100").child(firebaseUserPurchases.getUid());
                                            databaseReferencePurchases.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot d:snapshot.getChildren()){
                                                        if (d.child("purchase").getValue().toString().equals(firebaseUserPurchases.getUid())){
                                                            editorPurchase.putBoolean("buy100drawingtabuu",true);
                                                            Log.e("Shared log: ","saving100");
                                                            editorPurchase.commit();

                                                            //databasePurchases.getReference("");
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReferencePurchases = databasePurchases.getReference("purchases150").child(firebaseUserPurchases.getUid());
                                            databaseReferencePurchases.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot d:snapshot.getChildren()){
                                                        if (d.child("purchase").getValue().toString().equals(firebaseUserPurchases.getUid())){
                                                            editorPurchase.putBoolean("buy150classictabuu",true);
                                                            Log.e("Shared log: ","saving150");
                                                            editorPurchase.commit();

                                                            //databasePurchases.getReference("");
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReferencePurchases = databasePurchases.getReference("purchases300").child(firebaseUserPurchases.getUid());
                                            databaseReferencePurchases.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot d:snapshot.getChildren()){
                                                        if (d.child("purchase").getValue().toString().equals(firebaseUserPurchases.getUid())){
                                                            editorPurchase.putBoolean("buy300classictabuu",true);
                                                            Log.e("Shared log: ","saving300");
                                                            editorPurchase.commit();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReferencePurchases = databasePurchases.getReference("purchasesNoAds").child(firebaseUserPurchases.getUid());
                                            databaseReferencePurchases.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot d:snapshot.getChildren()){
                                                        if (d.child("purchase").getValue().toString().equals(firebaseUserPurchases.getUid())){
                                                            editorPurchase.putBoolean("buynoads",true);
                                                            Log.e("Shared log: ","savingads");
                                                            editorPurchase.commit();

                                                            //databasePurchases.getReference("");
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }


                                        progressBar2.setVisibility(View.INVISIBLE);
                                        tabLayout2.setVisibility(View.VISIBLE);
                                        viewPager2.setVisibility(View.VISIBLE);

                                        finish();
                                        startActivity(getIntent());
                                        //Toast.makeText(MainActivity.this,"Hesabınıza başarıyla giriş yapıldı",Toast.LENGTH_SHORT).show();


                                    }else {
                                        Toast.makeText(MainActivity.this,"Lütfen bilgilerinizi kontrol ediniz",Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                cardViewLogin.setVisibility(View.VISIBLE);
                                                cardViewSignUp.setVisibility(View.VISIBLE);
                                                progressBar2.setVisibility(View.INVISIBLE);
                                                tabLayout2.setVisibility(View.INVISIBLE);
                                                viewPager2.setVisibility(View.INVISIBLE);
                                            }
                                        },2000);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(MainActivity.this,"Lütfen bilgilerinizi kontrol ediniz",Toast.LENGTH_SHORT);
                            cardViewLogin.setVisibility(View.VISIBLE);
                            cardViewSignUp.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.INVISIBLE);
                        }


                        //Firebase işlemlerini burada yap
                        //dialog.dismiss();
                    }
                },2000);
            }
        });

        builder.create().show();

    }

    private void showAlertDialogForSignUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("ANLAT BAKALIM'A ÜYE OL");
        View view = getLayoutInflater().inflate(R.layout.alert_signup_design,null);

        inputEditTextSignName = view.findViewById(R.id.inputEditTextSignName);
        inputEditTextSignLastName = view.findViewById(R.id.inputEditTextSignLastName);
        inputEditTextSignEmail = view.findViewById(R.id.inputEditTextSignEmail);
        inputEditTextSignPassword = view.findViewById(R.id.inputEditTextSignPassword);


        builder.setView(view);

        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("ÜYE OL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputEditTextSignName.getText().toString().trim();
                String lastname = inputEditTextSignLastName.getText().toString().trim();
                String email = inputEditTextSignEmail.getText().toString().trim();
                String password = inputEditTextSignPassword.getText().toString(); //trim yapmamamın sebebi kullanıcı boşluk içerende karakter girebilir.


                if (!name.isEmpty() || !lastname.isEmpty() || !email.isEmpty() || !password.isEmpty()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //firebaseAuth = FirebaseAuth.getInstance();

                            if (!email.isEmpty()){
                                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(MainActivity.this,"Üyeliğiniz başarıyla oluşturuldu",Toast.LENGTH_SHORT).show();



                                            database = FirebaseDatabase.getInstance();
                                            String uid = String.valueOf(firebaseAuth.getUid());
                                            myRef = database.getReference("users").child(uid);
                                            Users user = new Users(name,lastname,email,uid);
                                            myRef.push().setValue(user);





                                        }else {
                                            Toast.makeText(MainActivity.this,"Lütfen bilgilerinizi kontrol ediniz",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }





                        }
                    },100);
                }




            }

        });

        builder.create().show();

    }

    void showAlertDialogForClassic(){


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("TAKIM OLUŞTUR");
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.alert_select_team,null);

        textInputEditTextClassicTeamA = view.findViewById(R.id.textInputEditTextTeamA);
        textInputEditTextClassicTeamB = view.findViewById(R.id.textInputEditTextTeamB);

        builder.setView(view);

        builder.setPositiveButton("OYNA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String teamClassicA = textInputEditTextClassicTeamA.getText().toString().trim();
                String teamClassicB = textInputEditTextClassicTeamB.getText().toString().trim();

                if (teamClassicA.isEmpty()){
                    teamClassicA = "A TAKIMI";
                }

                if (teamClassicB.isEmpty()){
                    teamClassicB = "B TAKIMI";
                }

                if (teamClassicA.equals(teamClassicB)){
                    Toast.makeText(MainActivity.this,"Takım isimleri aynı olamaz",Toast.LENGTH_SHORT).show();
                }else {
                    sharedPreferences = getSharedPreferences("TeamClassicNames",MODE_PRIVATE);
                    editor = sharedPreferences.edit();

                    editor.clear();

                    editor.putString("teamClassicA",teamClassicA);
                    editor.putString("teamClassicB",teamClassicB);

                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this,ClassicPlayActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },1000);
                }


            }
        });
        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }


    void showAlertDialogForDrawing(){


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("TAKIM OLUŞTUR");
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.alert_select_team,null);

        textInputEditTextDrawingTeamA = view.findViewById(R.id.textInputEditTextTeamA);
        textInputEditTextDrawingTeamB = view.findViewById(R.id.textInputEditTextTeamB);

        builder.setView(view);
        builder.setPositiveButton("OYNA", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String teamDrawingA = textInputEditTextDrawingTeamA.getText().toString().trim();
                String teamDrawingB = textInputEditTextDrawingTeamB.getText().toString().trim();

                if (teamDrawingA.isEmpty()){
                    teamDrawingA = "A TAKIMI";
                }

                if (teamDrawingB.isEmpty()){
                    teamDrawingB = "B TAKIMI";
                }

                if (teamDrawingA.equals(teamDrawingB)){
                    Toast.makeText(MainActivity.this,"Takım isimleri aynı olamaz",Toast.LENGTH_SHORT).show();
                }else {
                    sharedPreferences = getSharedPreferences("TeamDrawingNames",MODE_PRIVATE);
                    editor = sharedPreferences.edit();

                    editor.clear();

                    editor.putString("teamDrawingA",teamDrawingA);
                    editor.putString("teamDrawingB",teamDrawingB);

                    editor.commit();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this,DrawingPlayActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },1000);
                }


            }
        });
        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();



    }

    /*public void signOuted(){
        progressBar2.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout2.setVisibility(View.INVISIBLE);
                viewPager2.setVisibility(View.INVISIBLE);
                cardViewLogin.setVisibility(View.VISIBLE);
                cardViewSignUp.setVisibility(View.VISIBLE);
            }
        },2000);

    }*/



    private class MyViewPagerAdapter extends FragmentStateAdapter {

        public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentArrayList.size();
        }
    }


}
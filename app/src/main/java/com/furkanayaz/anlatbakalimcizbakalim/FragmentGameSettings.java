package com.furkanayaz.anlatbakalimcizbakalim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class FragmentGameSettings extends Fragment {
    private AdView adView;
    private View view;
    private TextView textViewSettingsSeconds,textViewSettingsPass,textViewSettingsTaboo;
    private SharedPreferences sharedPreferences;

    private TextView textViewGameSettingsSeconds,textViewGameSettingsPass,textViewGameSettingsTaboo;

    private SeekBar seekBarSettingsSeconds,seekBarSettingsPass,seekBarSettingsTaboo;
    private Button buttonSettingsUpdate;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_settings,container,false);

        textViewSettingsSeconds = view.findViewById(R.id.textViewSettingsSeconds);
        textViewSettingsPass = view.findViewById(R.id.textViewSettingsPass);
        textViewSettingsTaboo = view.findViewById(R.id.textViewSettingsTaboo);


        textViewGameSettingsSeconds = view.findViewById(R.id.textViewGameSettingsSeconds);
        textViewGameSettingsPass = view.findViewById(R.id.textViewGameSettingsPass);
        textViewGameSettingsTaboo = view.findViewById(R.id.textViewGameSettingsTaboo);


        seekBarSettingsSeconds = view.findViewById(R.id.seekBarSettingsSeconds);
        seekBarSettingsPass = view.findViewById(R.id.seekBarSettingsPass);
        seekBarSettingsTaboo = view.findViewById(R.id.seekBarSettingsTaboo);


        buttonSettingsUpdate = view.findViewById(R.id.buttonSettingsUpdate);

        sharedPreferences = sharedPreferences = getContext().getSharedPreferences("purchaseprocess",Context.MODE_PRIVATE);
        boolean buyprocess = sharedPreferences.getBoolean("buynoads",false);

        if (!buyprocess){
            MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });

            adView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }




        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ClassicSettings",Context.MODE_PRIVATE);

        int seconds = sharedPreferences.getInt("classicseconds",180);
        int pass = sharedPreferences.getInt("classicpass",5);
        int taboo = sharedPreferences.getInt("classictaboo",3);


        textViewGameSettingsSeconds.setText(String.valueOf(seconds));
        textViewGameSettingsPass.setText(String.valueOf(pass));
        textViewGameSettingsTaboo.setText(String.valueOf(taboo));
        seekBarSettingsSeconds.setProgress(seconds);
        seekBarSettingsPass.setProgress(pass);
        seekBarSettingsTaboo.setProgress(taboo);



        seekBarSettingsSeconds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress<45){
                    progress = 30;
                }
                if (progress<60 && progress>45){
                    progress = 60;
                }
                if (progress<90 && progress>60){
                    progress = 90;
                }
                if (progress<120 && progress>90){
                    progress = 120;
                }
                if (progress<150 && progress>120){
                    progress = 150;
                }
                if (progress<180 && progress>150){
                    progress = 180;
                }
                switch (progress%30){
                    case 0:
                        seekBarSettingsSeconds.setProgress(progress);
                        textViewGameSettingsSeconds.setText(String.valueOf(progress));
                        break;


                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSettingsPass.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSettingsPass.setProgress(progress);
                textViewGameSettingsPass.setText(String.valueOf(progress));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSettingsTaboo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSettingsTaboo.setProgress(progress);
                textViewGameSettingsTaboo.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        buttonSettingsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("ClassicSettings",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();

                editor.putInt("classicseconds",Integer.parseInt(textViewGameSettingsSeconds.getText().toString()));
                editor.putInt("classicpass",Integer.parseInt(textViewGameSettingsPass.getText().toString()));
                editor.putInt("classictaboo",Integer.parseInt(textViewGameSettingsTaboo.getText().toString()));
                editor.commit();






                Toast.makeText(getContext(),"Ayarlarınız kaydedildi",Toast.LENGTH_SHORT).show();
            }
        });




        return view;
    }

}

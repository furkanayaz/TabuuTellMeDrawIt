package com.furkanayaz.anlatbakalimcizbakalim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentProfile extends Fragment {
    private View view;
    private ImageView imageViewCharacter;
    private TextView textViewProfileNameAndLastname;
    private TextView textViewProfileEmail;
    private Button buttonProfileSignOut;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile,container,false);

        imageViewCharacter = view.findViewById(R.id.imageViewCharacter);
        textViewProfileNameAndLastname = view.findViewById(R.id.textViewProfileNameAndLastname);
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail);
        buttonProfileSignOut = view.findViewById(R.id.buttonProfileSignOut);

        sharedPreferences = view.getContext().getSharedPreferences("AVATAR", view.getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int changed = sharedPreferences.getInt("changed",0);

        if (changed == 0){
            editor.putInt("avatar",R.drawable.defaultavatar);
            editor.commit();

            imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.defaultavatar));
        }else {
            int avatar = sharedPreferences.getInt("avatar",R.drawable.defaultavatar);
            imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",avatar));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            firebaseUserControlProvider();
        }

        imageViewCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_choose_avatar,null,false);

                ImageButton imageButtonAvatar1 = view.findViewById(R.id.imageButtonAvatar1);
                ImageButton imageButtonAvatar2 = view.findViewById(R.id.imageButtonAvatar2);
                ImageButton imageButtonAvatar3 = view.findViewById(R.id.imageButtonAvatar3);
                ImageButton imageButtonAvatar4 = view.findViewById(R.id.imageButtonAvatar4);
                ImageButton imageButtonAvatar5 = view.findViewById(R.id.imageButtonAvatar5);
                ImageButton imageButtonAvatar6 = view.findViewById(R.id.imageButtonAvatar6);
                ImageButton imageButtonAvatar7 = view.findViewById(R.id.imageButtonAvatar7);
                ImageButton imageButtonAvatar8 = view.findViewById(R.id.imageButtonAvatar8);

                imageButtonAvatar1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar1);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar1));
                    }
                });

                imageButtonAvatar2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar2);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar2));
                    }
                });

                imageButtonAvatar3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar3);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar3));
                    }
                });

                imageButtonAvatar4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar4);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar4));
                    }
                });

                imageButtonAvatar5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar5);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar5));
                    }
                });

                imageButtonAvatar6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar6);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar6));
                    }
                });

                imageButtonAvatar7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar7);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar7));
                    }
                });

                imageButtonAvatar8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.clear();
                        editor.putInt("avatar",R.drawable.avatar8);
                        editor.putInt("changed",1);
                        editor.commit();

                        imageViewCharacter.setImageResource(sharedPreferences.getInt("avatar",R.drawable.avatar8));
                    }
                });

                builder.setTitle("AVATARINI SEÇ");
                builder.setView(view);
                builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
            }
        });

        buttonProfileSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(),SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return view;
    }


    private void firebaseUserControlProvider() {


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(firebaseUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d:snapshot.getChildren()){
                    if (d.child("uid").getValue().toString().equals(firebaseUser.getUid())){
                        Users user = d.getValue(Users.class);
                        String name = user.getName();
                        String lastname = user.getLastname();
                        String nameandlastname = name+" "+lastname;
                        String email = user.getEmail();

                        textViewProfileNameAndLastname.setText("Ad Soyad: "+nameandlastname);
                        textViewProfileEmail.setText("Email: "+email);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }




}

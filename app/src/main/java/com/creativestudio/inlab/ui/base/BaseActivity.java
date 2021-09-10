package com.creativestudio.inlab.ui.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.creativestudio.inlab.R;
import com.creativestudio.inlab.model.modellogin.ModelLogin;
import com.creativestudio.inlab.ui.login.ActivityLogin;
import com.creativestudio.inlab.utils.AppConsts;
import com.creativestudio.inlab.utils.sharedpref.SharedPref;


import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {


    Context mContext;
   ModelLogin modelLogin;
    SharedPref sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mContext = BaseActivity.this;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(modelLogin != null){
            if(modelLogin.getStudentData() != null){
                if(modelLogin.getStudentData().getLanguageName().equalsIgnoreCase("arabic")){
                    //manually set Directions

                    getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    Configuration configuration = getResources().getConfiguration();
                    configuration.setLayoutDirection(new Locale("fa"));
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                    String languageToLoad = "ar"; // your language
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                }

                if (modelLogin.getStudentData().getLanguageName().equalsIgnoreCase("french")) {
                    String languageToLoad = "fr"; // your language
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());


                }
                if (modelLogin.getStudentData().getLanguageName().equalsIgnoreCase("english")) {
                    String languageToLoad = "en"; // your language
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                }

            }

            checkLogin();
        }

    }
    private void checkLogin() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        if (modelLogin != null) {
                            if (modelLogin.getStudentData() != null) {

                                AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.CHECK_LOGIN)
                                        .addBodyParameter(AppConsts.STUDENT_ID, modelLogin.getStudentData().getStudentId())
                                        .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                                        .addBodyParameter(AppConsts.TOKEN, task.getResult().getToken())
                                        .setTag(AppConsts.CHECK_LOGIN)
                                        .setPriority(Priority.IMMEDIATE)
                                        .build()
                                        .getAsString(new StringRequestListener() {
                                            @Override
                                            public void onResponse(String response) {


                                                try {

                                                    JSONObject jsonObject = new JSONObject(response);

                                                    if ("false".equalsIgnoreCase(jsonObject.getString("status"))) {

                                                        sharedPref.clearAllPreferences();
                                                        Intent loginScreen = new Intent(mContext, ActivityLogin.class);
                                                        loginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(loginScreen);
                                                        finish();
                                                    }


                                                } catch (Exception e) {

                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                Toast.makeText(mContext, getResources().getString(R.string.Try_again), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {

                                Intent intent = new Intent(mContext, ActivityLogin.class);
                                intent.putExtra(AppConsts.IS_SPLASH, "true");
                                startActivity(intent);
                            }
                        } else {

                            Intent intent = new Intent(mContext, ActivityLogin.class);
                            intent.putExtra(AppConsts.IS_SPLASH, "true");
                            startActivity(intent);

                        }

                    }
                });


    }

    public String getCurrentDate(String outputFormat) {
        DateFormat dateFormat = new SimpleDateFormat(outputFormat);
        Date date = new Date();
        return dateFormat.format(date);
    }

}

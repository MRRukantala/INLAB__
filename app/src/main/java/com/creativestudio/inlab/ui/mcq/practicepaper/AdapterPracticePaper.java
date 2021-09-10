package com.creativestudio.inlab.ui.mcq.practicepaper;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.CountDownTimer;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.creativestudio.inlab.R;

import com.creativestudio.inlab.model.modellogin.ModelLogin;
import com.creativestudio.inlab.model.modelpracticepaper.ModelPracticePaper;
import com.creativestudio.inlab.ui.generatepapers.ActivityGetPapers;
import com.creativestudio.inlab.utils.AppConsts;

import com.creativestudio.inlab.utils.sharedpref.SharedPref;

import com.creativestudio.inlab.utils.widgets.CustomTextSemiBold;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

import java.util.concurrent.TimeUnit;

public class AdapterPracticePaper extends RecyclerView.Adapter<AdapterPracticePaper.AdapterPracticePaperViewHolder> {

    Context mContext;
    ArrayList<ModelPracticePaper.ExamPaper> practiceList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterPracticePaper(Context mContext, ArrayList<ModelPracticePaper.ExamPaper> practiceList) {
        this.mContext = mContext;
        this.practiceList = practiceList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
    }

    @NonNull
    @Override
    public AdapterPracticePaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_practice_paper, viewGroup, false);
        return new AdapterPracticePaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPracticePaperViewHolder holder, int i) {
        final ModelPracticePaper.ExamPaper data = practiceList.get(i);


        if ("mock_test".equals(data.getExamType())) {
            holder.tvStartPaper.setVisibility(View.GONE);
            holder.tvTimeExamName.setVisibility(View.VISIBLE);
            startCountDownTimer(holder.tvStartPaper, holder.countDownTimer, holder.tvTimeExamName, data.getMockSheduledDate() + " " + data.getMockSheduledTime(), data.getCurrentTime());
            holder.mockDate.setVisibility(View.VISIBLE);
            holder.mockDate.setText(mContext.getResources().getString(R.string.Date) + " : " + data.getMockSheduledDate() + " " + mContext.getResources().getString(R.string.At) + " : " + data.getMockSheduledTime());
        } else {
            holder.mockDate.setVisibility(View.GONE);
            holder.tvTimeExamName.setVisibility(View.GONE);
            holder.imgDateTime.setVisibility(View.GONE);
        }

        holder.tvExamName.setText(data.getName());
        holder.tvExamName.setTextSize(17f);
        holder.tvTime.setText(mContext.getResources().getString(R.string.Duration) + " : " + data.getTimeDuration() + " minutes");
        holder.tvQuestion.setText(mContext.getResources().getString(R.string.TotalQuestions) + " : " + data.getTotalQuestion() + "");
        holder.tvQuestion.setTextSize(13f);
        holder.tvTime.setTextSize(13f);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvTimeExamName.getVisibility() == View.GONE) {
                    dialogInstructions(practiceList.get(i));
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        if (practiceList != null)
            return practiceList.size();
        else
            return 0;
    }


    private String hmsTimeFormatter(long milliSeconds) {
        String sss = String.format("%02dhrs : %02dmin : %02dsec",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return sss.replaceAll("-", "");
    }


    private void startCountDownTimer(CustomTextSemiBold tvStartPaper, CountDownTimer ct, CustomTextSemiBold downName, String schTIME, String serverCurrentTime) {

        if (ct != null) {
            ct.cancel();
        }

        Date startDate = null;// Set start date
        Date endDate = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = sdf.parse(serverCurrentTime);
            endDate = sdf.parse(schTIME);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long duration = endDate.getTime() - startDate.getTime();


        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                downName.setText("Time left : " + hmsTimeFormatter(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                downName.setText("Start now");
                tvStartPaper.setVisibility(View.VISIBLE);
                downName.setVisibility(View.GONE);

            }
        }.start();
    }

    void dialogInstructions(ModelPracticePaper.ExamPaper data) {
        final Dialog dialog;
        dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_instructions);

        final Handler handler  = new Handler();

        CustomTextSemiBold dialogYesButton = dialog.findViewById(R.id.btnYes);
        ImageView ivBackDilog = dialog.findViewById(R.id.ivBackDilog);

        ivBackDilog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialogYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mContext.startActivity(new Intent(mContext, ActivityGetPapers.class)
                        .putExtra("id", "" + data.getId()).putExtra("nm", "" + data.getName()).putExtra(AppConsts.PAPER_TIME,
                                "" + data.getTimeDuration()));
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                };


                handler.postDelayed(runnable, 100);

            }
        });


        CustomTextSemiBold dialogNoButton = dialog.findViewById(R.id.btnNo);
        dialogNoButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    class AdapterPracticePaperViewHolder extends RecyclerView.ViewHolder {
        CustomTextSemiBold tvQuestion;
        CustomTextSemiBold tvExamName;
        CustomTextSemiBold tvStartPaper;
        CustomTextSemiBold tvTime;
        CustomTextSemiBold tvTimeExamName;
        CustomTextSemiBold mockDate;
        ImageView btStartPaper;
        ImageView imgDateTime;
        CountDownTimer countDownTimer;

        public AdapterPracticePaperViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamName = itemView.findViewById(R.id.tvExamName);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStartPaper = itemView.findViewById(R.id.tvStartPaper);
            tvTimeExamName = itemView.findViewById(R.id.tvTimeExamName);
            mockDate = itemView.findViewById(R.id.mockDate);
            imgDateTime = itemView.findViewById(R.id.imgDateTime);
            btStartPaper = itemView.findViewById(R.id.btStartPaper);

        }

    }


}

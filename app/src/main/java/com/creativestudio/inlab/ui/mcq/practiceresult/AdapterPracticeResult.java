package com.creativestudio.inlab.ui.mcq.practiceresult;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativestudio.inlab.R;
import com.creativestudio.inlab.model.modellogin.ModelLogin;
import com.creativestudio.inlab.model.modelpractiesresult.ModelPractiesResult;
import com.creativestudio.inlab.ui.mcq.resultquestionlist.ActivityPaperResultList;
import com.creativestudio.inlab.utils.AppConsts;
import com.creativestudio.inlab.utils.sharedpref.SharedPref;
import com.creativestudio.inlab.utils.widgets.CustomTextNormalBold;
import com.creativestudio.inlab.utils.widgets.CustomTextSemiBold;
import com.creativestudio.inlab.utils.widgets.CustomeTextRegularBold;

import java.util.ArrayList;

public class AdapterPracticeResult extends RecyclerView.Adapter<AdapterPracticeResult.AdapterPracticeResultViewHolder> {

    Context mContext;
    String examType;
    String studentId;
    ArrayList<ModelPractiesResult.PracticeResult> testResultList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterPracticeResult(Context mContext, ArrayList<ModelPractiesResult.PracticeResult> testResultList, String examType, String studentId) {
        this.mContext = mContext;
        this.examType = examType;
        this.studentId = studentId;
        this.testResultList = testResultList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
    }

    @NonNull
    @Override
    public AdapterPracticeResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_paper_result_list, viewGroup, false);
        return new AdapterPracticeResultViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterPracticeResultViewHolder holder, int i) {
        ModelPractiesResult.PracticeResult data = testResultList.get(i);
        try {
            if (examType.equals("mock_test")) {
                holder.btViewRanks.setVisibility(View.VISIBLE);
            } else {
                holder.btViewRanks.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

        holder.tvPaperName.setText("" + data.getPaperName());


        try {

            holder.tvPercent.setText(data.getPercentage() + "%");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            holder.tvTimeTaken.setText("" + data.getTimeTaken());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (examType.equals("mock_test")) {

            holder.tvExamDate.setText(mContext.getResources().getString(R.string.Date)+" : " + data.getDate() + " "+mContext.getResources().getString(R.string.At)+" : " + data.getScheduleTime());
            holder.tvAttemptQues.setText("  " + data.getAttemptedQuestion() + " / " + data.getTotalQuestion());

        } else {
            holder.tvExamDate.setText(mContext.getResources().getString(R.string.Date)+" : " + data.getDate());
            holder.tvAttemptQues.setText("  " + data.getAttemptedQuestion() + " / " + data.getTotalQuestion());
        }


        if (data.getResultId().equalsIgnoreCase("0")) {
            if (examType.equals("mock_test")) {
                holder.tvExamDate.setText(mContext.getResources().getString(R.string.Date)+" : " + data.getDate() +" " + mContext.getResources().getString(R.string.At)+" : " + data.getScheduleTime() + "\n"+mContext.getResources().getString(R.string.Notattempted));
            } else {
                holder.tvExamDate.setText(mContext.getResources().getString(R.string.Date)+" : " + data.getDate() + "\n"+mContext.getResources().getString(R.string.Notattempted));
            }

            holder.tvExamTime.setText("0");


        } else {
            holder.tvExamTime.setText(data.getStartTime());
        }



        try {
            holder.tvRightAns.setText(data.getRightAns());

            holder.btViewRanks.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.btDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(mContext, ActivityPaperResultList.class);
                    intent.putExtra(AppConsts.EXAM_TYPE, examType);
                    intent.putExtra(AppConsts.PAPER_ID, data.getPaperId());
                    intent.putExtra(AppConsts.EXAM_NAME, data.getPaperName());
                    intent.putExtra(AppConsts.SHOW_RESULT, "yes");
                    intent.putExtra(AppConsts.PERCENTAGE, "" + holder.tvPercent.getText().toString());
                    intent.putExtra(AppConsts.TIME_TAKEN, "" + holder.tvTimeTaken.getText().toString());
                    intent.putExtra(AppConsts.EXAM_DATE, data.getDate());
                    intent.putExtra(AppConsts.PAPER_DATA, data);
                    intent.putExtra(AppConsts.RESULT_ID, "" + data.getResultId());
                    intent.putExtra(AppConsts.STUDENT_ID, data.getStudentId());
                    mContext.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return testResultList.size();
    }

    class AdapterPracticeResultViewHolder extends RecyclerView.ViewHolder {

        CustomTextNormalBold tvPaperName;
        CustomTextNormalBold tvExamDate;
        CustomTextSemiBold tvTimeTaken;
        CustomTextSemiBold tvAttemptQues;
        CustomTextSemiBold tvNegMarks;
        CustomTextSemiBold tvPercent;
        CustomTextSemiBold tvRightAns;
        CustomTextSemiBold tvExamTime;
        CustomeTextRegularBold btViewRanks;
        CustomeTextRegularBold btDetails;
        CustomTextNormalBold hideByAdmin;
        LinearLayout hideLayoutParent;

        public AdapterPracticeResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaperName = itemView.findViewById(R.id.tvPaperName);
            tvExamTime = itemView.findViewById(R.id.tvExamTime);
            tvTimeTaken = itemView.findViewById(R.id.tvTimeTaken);
            hideByAdmin = itemView.findViewById(R.id.hideByAdmin);
            tvAttemptQues = itemView.findViewById(R.id.tvAttemptQues);
            tvNegMarks = itemView.findViewById(R.id.tvNegMarks);
            tvRightAns = itemView.findViewById(R.id.tvRightAns);
            tvExamDate = itemView.findViewById(R.id.tvExamDate);
            tvPercent = itemView.findViewById(R.id.tvPercent);
            btDetails = itemView.findViewById(R.id.btDetails);
            hideLayoutParent = itemView.findViewById(R.id.hideLayoutParent);
        }
    }
}

package com.creativestudio.inlab.ui.home.adapter;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creativestudio.inlab.R;
import com.creativestudio.inlab.model.modellogin.ModelLogin;

import com.creativestudio.inlab.ui.attendance.ActivityAttendance;
import com.creativestudio.inlab.ui.doubtClasses.ActivityDoubtClasses;
import com.creativestudio.inlab.ui.extraclass.ActivityExtraClass;

import com.creativestudio.inlab.ui.galary.galleryvideos.ActivityGalleryVideos;
import com.creativestudio.inlab.ui.assignment.ActivityAssignment;
import com.creativestudio.inlab.ui.live.ActivityLive;

import com.creativestudio.inlab.ui.mcq.ActivityMCQDashboard;

import com.creativestudio.inlab.ui.noticeAnnouncement.ActivityForFragments;


import com.creativestudio.inlab.ui.payment.ActivityPaymentHistory;
import com.creativestudio.inlab.ui.UpcomingExams.ActivityVacancyOrUpcomingExam;

import com.creativestudio.inlab.utils.AppConsts;

import com.creativestudio.inlab.utils.sharedpref.SharedPref;
import com.creativestudio.inlab.utils.widgets.CustomTextSemiBold;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HolderHomeAdapter> {
    private Context mContext;
    private ArrayList<String> listHome;
    SharedPref sharedPref;
    String numberMeeting = "";
    String passwordMeeting = "";
    String sdkKey = "";
    String secretKey = "";
    ModelLogin modelLogin;


    public HomeAdapter(Context mContext, ArrayList<String> listHome) {
        this.mContext = mContext;
        this.listHome = listHome;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
        apiMeetingData();

    }

    @NonNull
    @Override
    public HolderHomeAdapter onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_home, parent, false);
        return new HomeAdapter.HolderHomeAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderHomeAdapter holder, final int position) {
        if (position == 0) {

            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.announcment));
        } else if (position == 2) {
            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.multichocie));

        } else if (position == 3) {
            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vacancies));
        } else if (position == 4) {

            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video));
        } else if (position == 5) {

            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hoemwork));
        } else if (position == 6) {

            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.extra_class));
        } else if (position == 7) {

            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.attendance));
        }
//        else if (position == 8) {
//            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.doubtclasses));
//        } else if (position == 7) {
//            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pay_icon));}
        else if (position == 1) {
            holder.ivTicket.setImageDrawable(mContext.getResources().getDrawable(R.drawable.online_class));

        }
        holder.tvTittle.setText(listHome.get(position));

        holder.rlBackSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0 && holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.Announcements))) {


                    mContext.startActivity(new Intent(mContext, ActivityForFragments.class));
                } else if (position == 1 && holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.Live_class))) {

                    if (!numberMeeting.isEmpty()) {
                        mContext.startActivity(new Intent(mContext, ActivityLive.class).
                                putExtra("meetingId", "" + numberMeeting).putExtra("meetingPassword", "" + passwordMeeting).putExtra("sdkKey", "" + sdkKey)
                                .putExtra("sdkSecret", "" + secretKey));
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.NoClassAvailable), Toast.LENGTH_SHORT).show();
                    }
                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.mcq))) {
                    mContext.startActivity(new Intent(mContext, ActivityMCQDashboard.class));
                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.Upcoming_Exams))) {
                    mContext.startActivity(new Intent(mContext, ActivityVacancyOrUpcomingExam.class));
                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.VideoLectures))) {
                    mContext.startActivity(new Intent(mContext, ActivityGalleryVideos.class));
                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.Assignment))) {
                    mContext.startActivity(new Intent(mContext, ActivityAssignment.class));
                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.ExtraClass))) {
                    mContext.startActivity(new Intent(mContext, ActivityExtraClass.class));
                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.Attendance))) {
                    mContext.startActivity(new Intent(mContext, ActivityAttendance.class));
                }
//                else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.DoubtClasses))) {
//                    mContext.startActivity(new Intent(mContext, ActivityDoubtClasses.class));
//                } else if (holder.tvTittle.getText().equals(mContext.getResources().getString(R.string.Payment))) {
//                    mContext.startActivity(new Intent(mContext, ActivityPaymentHistory.class));
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHome.size();
    }

    public class HolderHomeAdapter extends RecyclerView.ViewHolder {

        LinearLayout rlBackSupport;
        CustomTextSemiBold tvTittle;
        ImageView ivTicket;

        public HolderHomeAdapter(@NonNull View itemView) {
            super(itemView);
            rlBackSupport = (LinearLayout) itemView.findViewById(R.id.rlBackSupport);
            tvTittle = itemView.findViewById(R.id.tvTittle);
            ivTicket = (ImageView) itemView.findViewById(R.id.ivTicket);
        }
    }


    void apiMeetingData() {

        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_LIVE_CLASS_DATA)
                .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (AppConsts.TRUE.equals("" + jsonObject.getString("status"))) {
                                JSONArray jsonArray = new JSONArray("" + jsonObject.getString("data"));
                                JSONObject jsonObject1 = new JSONObject("" + jsonArray.get(0));
                                if (jsonObject1.has("meetingNumber")) {
                                    numberMeeting = "" + jsonObject1.getString("meetingNumber");
                                    passwordMeeting = "" + jsonObject1.getString("password");
                                    sdkKey = "" + jsonObject1.getString("sdkKey");
                                    secretKey = "" + jsonObject1.getString("sdkSecret");
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {


                    }
                });
    }

}

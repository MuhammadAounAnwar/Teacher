package com.ono.cas.teacher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ono.cas.teacher.R;
import com.ono.cas.teacher.interfaces.ISendData;
import com.ono.cas.teacher.janusclientapi.JanusPluginHandle;
import com.ono.cas.teacher.utils.UtilityFunctions;

import java.math.BigInteger;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DummyJanusStreamsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<JanusPluginHandle> janusPluginHandles;
    private ISendData sendData;
    private BigInteger pubHandleId;
    public static final String TAG = "JanusStreamsAdapter";

    public DummyJanusStreamsAdapter(Context context, List<JanusPluginHandle> janusPluginHandles, ISendData sendData) {
        mContext = context;
        this.janusPluginHandles = janusPluginHandles;
        this.sendData = sendData;
    }

    public DummyJanusStreamsAdapter(Context context, List<JanusPluginHandle> janusPluginHandles, ISendData sendData, BigInteger pubHandleId) {
        mContext = context;
        this.janusPluginHandles = janusPluginHandles;
        this.sendData = sendData;
        this.pubHandleId = pubHandleId;
    }

    public void setPubHandleId(BigInteger pubHandleId) {
        this.pubHandleId = pubHandleId;
    }

    @Override
    public int getItemCount() {
        return janusPluginHandles.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_janus_call_renderer_item, null, false);
        return new CallViewHolder(view);
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        JanusPluginHandle janusPluginHandle = janusPluginHandles.get(position);
        ((CallViewHolder) holder).bind(janusPluginHandle);
        ((CallViewHolder) holder).janusPluginHandle = janusPluginHandle;
        ((CallViewHolder) holder).position = position;

        holder.itemView.setVisibility(View.VISIBLE);
        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }

    private class CallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout rv_surfaceViewRenderer;
        TextView txt_handle_name;
        JanusPluginHandle janusPluginHandle;
        int position = -1;

        CallViewHolder(View itemView) {
            super(itemView);
            rv_surfaceViewRenderer = itemView.findViewById(R.id.rv_surfaceViewRenderer);
            txt_handle_name = itemView.findViewById(R.id.txt_handle_name);
            rv_surfaceViewRenderer.setOnClickListener(this);
        }

        void bind(JanusPluginHandle janusPluginHandle) {
            /*
             * Check if remote user video is available or not.
             * */
            if (janusPluginHandle.janusUserDetail.isVideoEnable()) {
                View surfaceView = janusPluginHandle.janusUserDetail.getView();
                if (null != surfaceView) {
                    if (null != surfaceView.getParent()) {
                        ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
                    }
                    rv_surfaceViewRenderer.removeAllViews();
                    rv_surfaceViewRenderer.addView(surfaceView);
                    surfaceView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                }
            } else {
                rv_surfaceViewRenderer.removeAllViews();
            }

            /*
             * Check if display name is available or not.
             * */
            if (!TextUtils.isEmpty(janusPluginHandle.janusUserDetail.getUserName())) {
                txt_handle_name.setText(janusPluginHandle.janusUserDetail.getUserName());
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    private void configureLayout(LinearLayout viewLayout, ConstraintLayout nameLayout, boolean isAudio) {
        if (isAudio) {
            viewLayout.setVisibility(View.INVISIBLE);
            nameLayout.setVisibility(View.VISIBLE);
        } else {
            viewLayout.setVisibility(View.VISIBLE);
            nameLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void updateList(List<JanusPluginHandle> janusPluginHandles) {
        this.janusPluginHandles = janusPluginHandles;
//        Log.d(TAG, "updateList: " + this.janusPluginHandles.toString());
        notifyDataSetChanged();
    }

    public void addRenderer(JanusPluginHandle janusPluginHandle) {
        janusPluginHandles.add(janusPluginHandle);
        notifyItemInserted(janusPluginHandles.size() - 1);
    }

    public void updateRenderer(int index, JanusPluginHandle janusPluginHandle) {
//        Log.d(TAG, "updateRenderer: " + String.valueOf(index)+"janusPluginHandles Size:"+String.valueOf(janusPluginHandles.size()));
        if (index < janusPluginHandles.size()) {
            janusPluginHandles.set(index, janusPluginHandle);
            notifyItemChanged(index);
        }
    }

    public void removeRendererViews(int index) {
        if (index < janusPluginHandles.size()) {
            JanusPluginHandle janusPluginHandle = janusPluginHandles.get(index);
        }
    }

    public void removeRendererByIndex(int index) {
        if (index < janusPluginHandles.size()) {
            janusPluginHandles.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeRendererByObject(JanusPluginHandle janusPluginHandle) {
        janusPluginHandles.remove(janusPluginHandle);
        notifyDataSetChanged();
    }

    public int getHandlerIndexFromAdapter(BigInteger id) {
        for (int i = 0; i < janusPluginHandles.size(); i++) {
            JanusPluginHandle janusPluginHandle = janusPluginHandles.get(i);

            if (id.equals(janusPluginHandle.janusUserDetail.getFeedId())) {
                return i;
            }
        }
        return -1;
    }

    public JanusPluginHandle getHandleFromAdapter(BigInteger id) {
        for (JanusPluginHandle janusPluginHandle : janusPluginHandles) {
            if (id.equals(janusPluginHandle.janusUserDetail.getFeedId())) {
                return janusPluginHandle;
            }
        }
        return null;
    }
}
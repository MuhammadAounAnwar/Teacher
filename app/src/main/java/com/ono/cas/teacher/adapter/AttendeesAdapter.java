package com.ono.cas.teacher.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ono.cas.teacher.R;
import com.ono.cas.teacher.utils.UtilityFunctions;
import com.ono.cas.teacher.dto.dtoContact;
import com.ono.cas.teacher.dto.dtoDevice;
import com.ono.cas.teacher.janusclientapi.JanusPluginHandle;

import java.util.List;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.ViewHolder> {

    private List<Object> mItems;
    private ItemListener mListener;

    public AttendeesAdapter(List<Object> items, ItemListener listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.position = position;
        Object object = mItems.get(position);
        if (object instanceof dtoDevice) {
            dtoDevice device = (dtoDevice) object;
            holder.setLocalContact(device);
        } else if (object instanceof dtoContact) {
            dtoContact contact = (dtoContact) object;
            holder.setOnlineContact(contact);
        } else if (object instanceof JanusPluginHandle) {
            JanusPluginHandle janusPluginHandle = (JanusPluginHandle) object;
            holder.setJanusPluginHandle(janusPluginHandle);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView profileImage;
        TextView profileName, cardView_Text;
        ConstraintLayout constraintLayout_Contact;
        dtoDevice item;
        dtoContact contact;
        JanusPluginHandle janusPluginHandle;
        ImageView img_mic;
        int position;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            profileName = itemView.findViewById(R.id.textView_Contact);
            profileImage = itemView.findViewById(R.id.cardView);
            cardView_Text = itemView.findViewById(R.id.cardView_Text);
            constraintLayout_Contact = itemView.findViewById(R.id.constraintLayout_Contact);
            img_mic = itemView.findViewById(R.id.img_mic);

            img_mic.setOnClickListener(this);
            constraintLayout_Contact.setOnClickListener(this);
            cardView_Text.setOnClickListener(this);
            profileImage.setOnClickListener(this);
            profileName.setOnClickListener(this);

        }

        void setLocalContact(dtoDevice item) {
            this.item = item;
            profileName.setText(item.getName() + "  -  " + item.getDevtype());
            cardView_Text.setText(UtilityFunctions.getUserNameInitials(item.getName().trim()));
        }

        void setOnlineContact(dtoContact contact) {
            this.contact = contact;
            profileName.setText(contact.getName());
            cardView_Text.setText(UtilityFunctions.getUserNameInitials(contact.getName().trim()));
        }

        void setJanusPluginHandle(JanusPluginHandle janusPluginHandle) {
            this.janusPluginHandle = janusPluginHandle;
            profileName.setText(janusPluginHandle.janusUserDetail.getUserName());
            cardView_Text.setText(UtilityFunctions.getUserNameInitials(janusPluginHandle.janusUserDetail.getUserName().trim()));

            configureMic(janusPluginHandle.janusUserDetail.isSelfMute());
            img_mic.setVisibility(View.VISIBLE);
            img_mic.setEnabled(true);
            img_mic.setFocusable(true);
        }

        private void configureMic(boolean isMicEnable) {
            if (isMicEnable) {
                img_mic.setImageResource(R.mipmap.mic_disable);
            } else {
                img_mic.setImageResource(R.mipmap.mic);
            }
        }

        @Override
        public void onClick(View v) {
            if (null != mListener) {
                if (v.equals(constraintLayout_Contact) || v.equals(profileName) || v.equals(cardView_Text)) {
                    mListener.onItemClick(mItems.get(position), position);
                } else if (v.equals(img_mic)) {
                    if (0 != position) {
                        mListener.onMicClick(mItems.get(position), position);
                    }
                }
            }
        }
    }

    public void updateList(List<Object> objects) {
        mItems = objects;
        notifyDataSetChanged();
    }

    public void addContactsToList(List<Object> objects) {
        mItems.addAll(objects);
        notifyDataSetChanged();
    }

    public void updateAttendeeByIndex(int index, Object object) {
        if (index < mItems.size()) {
            mItems.set(index, object);
            notifyItemChanged(index);
        }
    }

    public void removeAttendeeByIndex(int index) {
        if (index < mItems.size()) {
            mItems.remove(index);
            notifyItemRemoved(index);
        }
    }

    public interface ItemListener {
        void onItemClick(@NonNull Object object, int index);

        void onMicClick(@NonNull Object object, int index);
    }

    public void filterContacts(String searchText) {
//        if (null != mItems && !mItems.isEmpty()) {
//            searchText = searchText.toLowerCase(Locale.getDefault());
//            mItems.clear();
//
//            List<dtoDevice> availableDevices = DatabaseUtil.getAvailableDevices();
//            if (null != availableDevices && !availableDevices.isEmpty()) {
//                if (searchText.isEmpty()) {
//                    mItems.addAll(availableDevices);
//                } else {
//
//                    for (int i = 0; i < availableDevices.size(); i++) {
//                        if (availableDevices.get(i).getName().toLowerCase(Locale.getDefault()).contains(searchText)) {
//                            mItems.add(availableDevices.get(i));
//                        }
//                    }
//                }
//                notifyDataSetChanged();
//            }
//        }
    }
}

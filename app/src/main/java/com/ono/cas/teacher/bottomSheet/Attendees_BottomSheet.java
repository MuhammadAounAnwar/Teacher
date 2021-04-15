package com.ono.cas.teacher.bottomSheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ono.cas.teacher.adapter.AttendeesAdapter;
import com.ono.cas.teacher.utils.GlobalData;
import com.ono.cas.teacher.utils.MyLifecycleHandler;
import com.ono.cas.teacher.interfaces.OnClickAttendee;
import com.ono.cas.teacher.R;
import com.ono.cas.teacher.utils.UtilityFunctions;
import com.ono.cas.teacher.janusclientapi.JanusPluginHandle;

import java.util.ArrayList;
import java.util.List;

public class Attendees_BottomSheet
        extends BottomSheetDialogFragment
        implements AttendeesAdapter.ItemListener {


    private AttendeesAdapter attendeesAdapter;
    private RecyclerView recyclerView;
    private Activity activity;
    private ImageView imageView_Copy, imageView_Search, imageView_inviteLink_bg;
    private TextView textView_InviteLink;
    private EditText editTextText_SearchView;
    private String invitationLink;
    OnClickAttendee onClickAttendee;
    private boolean setInviteCall;
    private static final String TAG = "Invite_BottomSheet";
    public List<JanusPluginHandle> janusPluginHandles = new ArrayList<>();

    public Attendees_BottomSheet(Activity activity, String invitationLink, OnClickAttendee onClickAttendee) {
        this.activity = activity;
        this.invitationLink = invitationLink;
        this.onClickAttendee = onClickAttendee;
    }

    public Attendees_BottomSheet(Activity activity, String invitationLink, OnClickAttendee onClickAttendee, boolean setInviteCall) {
        this.activity = activity;
        this.invitationLink = invitationLink;
        this.onClickAttendee = onClickAttendee;
        this.setInviteCall = setInviteCall;
    }

    public Attendees_BottomSheet(Activity activity, String invitationLink, OnClickAttendee onClickAttendee, boolean setInviteCall,
                                 List<JanusPluginHandle> janusPluginHandles) {
        this.activity = activity;
        this.invitationLink = invitationLink;
        this.onClickAttendee = onClickAttendee;
        this.setInviteCall = setInviteCall;
        this.janusPluginHandles = janusPluginHandles;
    }

    public BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.invite_bottom_sheet, null);
        dialog.setContentView(contentView);

        imageView_Copy = contentView.findViewById(R.id.imageView_Copy);
        textView_InviteLink = contentView.findViewById(R.id.textView_InviteLink);
        imageView_inviteLink_bg = contentView.findViewById(R.id.imageView_inviteLink_bg);

        imageView_Copy.setOnClickListener(view -> {
            GlobalData.isShareApp = true;
            ClipboardManager clipboard = (ClipboardManager) MyLifecycleHandler.activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Trango : Invitation Link", textView_InviteLink.getText().toString().trim());
            clipboard.setPrimaryClip(clip);

            Log.d(TAG, "setupDialog: RoomId: " + invitationLink);
            UtilityFunctions.shareInviteLink(invitationLink);

            Toast.makeText(MyLifecycleHandler.activity, "Invitation link has been copied.", Toast.LENGTH_SHORT).show();
        });

        textView_InviteLink.setText(invitationLink);
        imageView_Search = contentView.findViewById(R.id.imageView_Search);
        editTextText_SearchView = contentView.findViewById(R.id.editTextText_SearchView);

        imageView_Search.setOnClickListener(view -> {

        });
        editTextText_SearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (null != janusPluginHandles && !janusPluginHandles.isEmpty()) {
                    attendeesAdapter.filterContacts(cs.toString());
                } else {
                    Toast.makeText(activity, "No contact found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (null != janusPluginHandles && !janusPluginHandles.isEmpty()) {
                    attendeesAdapter.filterContacts(arg0.toString());
                } else {
                    Toast.makeText(activity, "No contact found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setRecyclerView(contentView);
    }

    private void setRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.Contacts_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyLifecycleHandler.activity));
        attendeesAdapter = new AttendeesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(attendeesAdapter);
        attendeesAdapter.addContactsToList((List) janusPluginHandles);
    }

    @Override
    public int getTheme() {
        return R.style.MyBottomSheetDialogTheme;
    }

    @Override
    public void onItemClick(@NonNull Object object, int index) {
        onClickAttendee.onAttendeeSelected(object, index);
        dismiss();
    }

    @Override
    public void onMicClick(@NonNull Object object, int index) {
        JanusPluginHandle janusPluginHandle = janusPluginHandles.get(index);
        boolean isSelfMute = janusPluginHandle.janusUserDetail.isSelfMute();
        if (isSelfMute) {
            janusPluginHandle.remoteStream.audioTracks.get(0).setEnabled(true);
            janusPluginHandle.janusUserDetail.setSelfMute(false);
        } else {
            janusPluginHandle.remoteStream.audioTracks.get(0).setEnabled(false);
            janusPluginHandle.janusUserDetail.setSelfMute(true);
        }

        attendeesAdapter.updateAttendeeByIndex(index, janusPluginHandle);
        onClickAttendee.onMicClick(janusPluginHandle, index);
    }

}

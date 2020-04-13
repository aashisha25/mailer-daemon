package com.mailerdaemon.app.Notices;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mailerdaemon.app.R;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import Utils.AccessDatabse;
import Utils.ImageUploadCallBack;
import Utils.StringRes;
import Utils.UploadData;
import Utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AddNoticeFragment extends DialogFragment implements ViewUtils.showProgressBar, ImageUploadCallBack {
  public AddNoticeFragment(){

  }

  private ImageButton imageButton;
  private ImageView imageView;
  private EditText heading;
  private TextInputEditText detail;
  private ImageButton send;
  private String path=null;
  private String downloadUrl=null;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.bt_close)
  ImageButton close;
  private int PERMISSION_CAMERA=121;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view =inflater.inflate(R.layout.fragment_add_notice,container,false);
    ButterKnife.bind(this,view);
    imageButton=view.findViewById(R.id.bt_img);
    imageView=view.findViewById(R.id.image);
    heading=view.findViewById(R.id.head);
    detail=view.findViewById(R.id.detail);
    send=view.findViewById(R.id.send);


    imageButton.setOnClickListener(v -> {
      if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA)==
      PackageManager.PERMISSION_DENIED)
      {
        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
          Toast.makeText(getContext(),"In case you need Camera to upload picture the app needs this permission",Toast.LENGTH_LONG).show();
        }
          ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
      }else {
        EasyImage.openChooserWithGallery(this, "Pic image", EasyImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
        EasyImage.configuration(getContext()).allowsMultiplePickingInGallery();
      }});

    send.setOnClickListener(v ->{
      changeProgressBar();
      UploadData.upload(this, path, getContext());
    });
    close.setOnClickListener(v->{
      dismiss();
    });

    return view;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode==PERMISSION_CAMERA){
if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
  EasyImage.openChooserWithGallery(this, "Pic image", EasyImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
  EasyImage.configuration(Objects.requireNonNull(getContext())).allowsMultiplePickingInGallery();
}
    }
  }

  private void setDatabase() {
    Date date=new Date();
//    DateFormat dateFormat=new SimpleDateFormat("hh:mm aaa  dd.MM.yy");
    NoticeModel noticeModel=new NoticeModel();
    noticeModel.setDate(date);
    noticeModel.setDetails(Objects.requireNonNull(detail.getText()).toString());
    noticeModel.setHeading(heading.getText().toString());
    noticeModel.setPhoto(downloadUrl);
    FirebaseFirestore.getInstance().collection(StringRes.FB_Collec_Notice).document().set(noticeModel);
    changeProgressBar();
    dismiss();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
      @Override
      public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
        imageView.setImageURI(Uri.fromFile(imageFiles.get(0)));
        path=imageFiles.get(0).getPath();
      }
    });
  }

  @Override
  public void onSuccess(String downloadUrl) {
    this.downloadUrl=downloadUrl;
    setDatabase();
  }

  @Override
  public void changeProgressBar() {
    if(progressBar.isShown())
    {
      progressBar.setVisibility(View.GONE);
    }
    else progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {
    super.onDismiss(dialog);
    AccessDatabse method=(AccessDatabse)getActivity();
    method.getDatabase();
  }

}

package sany.com.mmpapp.business.government.firstActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;


import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sany.com.mmpapp.BuildConfig;
import sany.com.mmpapp.Manifest;
import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.Util.CutPictureUtils;
import sany.com.mmpapp.Util.FileStorage;
import sany.com.mmpapp.Util.GetImagePath;
import sany.com.mmpapp.Util.PermissionsActivity;
import sany.com.mmpapp.Util.PermissionsChecker;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;



public class AlarmDealActivity extends Activity {

    private MmpApp mmpApp;
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private TextView tv_RightBtn;
    private TextView tv_sfName;
    private TextView tv_eiName;
    private TextView tv_paraNameShow;
    private TextView tv_startTime;
    private TextView tv_dealer;
    private TextView tv_content;
    private TextView tv_contentcheck;
    private TextView tv_dealTime;
    private EditText et_lawer_content;
    private TextView tv_choicepicture;
    private TextView tv_getpicture;
    private ImageView iv_picturedisplay;
    private Button btn_submitpicture;
    private File tempFile = new File(Environment.getExternalStorageDirectory()+"/mmpapp/",getPhotoFileName());

    private File imageTemp=new File(Environment.getExternalStorageDirectory()+"/mmpapp/","temp.jpg");
    private String tempFileName;
    private int alarmid;
    private String phoneNum;
    private String evVehiNo;
    private String sfName;
    private String eiName;
    private String startTime;
    private String paraNameShow;
    private String dealer;
    private String dealTime;
    private  String statusName;
    private String content;

    private String newName ="image.jpg";

    private JSONObject jsonRep;



    /**
     * 调用相机
     */
    private final int TOCAMERA = 1;
    /**
     * 调用相册
     */
    private final int TOGALLERY = 2;
    /**
     * 调用裁剪
     */
    private final int TOCROP = 3;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    CutPictureUtils cutPictureUtils;
    public final static int SIG_GOOD_REC = 0x11;
    Uri uriContext=null;
    //5.11
    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求


    private PermissionsChecker mPermissionsChecker; // 权限检测器
    static final String[] PERMISSIONS = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA};
    private Uri imageUri;//原图保存地址
    private boolean isClickCamera;
    private String imagePath;

    private Uri outputUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_deal);
        mmpApp=(MmpApp)getApplication();


        tv_LeftBtn=(TextView)findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_RightBtn=(TextView)findViewById(R.id.right_titletop);
        tv_RightBtn.setText("位置");
        frameLayout=(FrameLayout)findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title=(TextView)findViewById(R.id.module_title);
        tv_Title.setText("违规处理");
        Intent intent=getIntent();
        alarmid=intent.getIntExtra("alarmid", 0);
        phoneNum=intent.getStringExtra("phoneNum");
        evVehiNo=intent.getStringExtra("evVehiNo");
        sfName=intent.getStringExtra("sfName");
        eiName=intent.getStringExtra("eiName");
        startTime=intent.getStringExtra("startTime");
        paraNameShow=intent.getStringExtra("paraNameShow");
        dealer=intent.getStringExtra("dealer");
        dealTime=intent.getStringExtra("dealTime");
        statusName=intent.getStringExtra("statusName");
        content=intent.getStringExtra("content");
        //content="aefgsdfgsdfgsdfgsdfgsdfgsdfgsdfgadfgsdafgsdfgasdfasdfasdfasdfwersadf克林霉素若干枯干枯干枯干枯干枯干";
        tv_sfName=(TextView)findViewById(R.id.sfName);
        tv_eiName=(TextView)findViewById(R.id.eiName);
        tv_paraNameShow=(TextView)findViewById(R.id.paraNameShow);
        tv_startTime=(TextView)findViewById(R.id.startTime);
        tv_dealer=(TextView)findViewById(R.id.dealer);
        tv_content=(TextView)findViewById(R.id.content);
        tv_contentcheck=(TextView)findViewById(R.id.contentcheck);
        tv_dealTime=(TextView)findViewById(R.id.dealTime);
        et_lawer_content=(EditText)findViewById(R.id.lawer_content);
        tv_choicepicture=(TextView)findViewById(R.id.choicepicture);
        tv_getpicture=(TextView)findViewById(R.id.getpicture);
        iv_picturedisplay=(ImageView)findViewById(R.id.picturedisplay);
        btn_submitpicture=(Button)findViewById(R.id.submitpicture);
        btn_submitpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() { //开启线程上传文件
                    @Override
                    public void run() {
                        submitpicture();
                    }
                }).start();



            }
        });
        cutPictureUtils=new CutPictureUtils(AlarmDealActivity.this);
        tv_sfName.setText(sfName);
        tv_eiName.setText(eiName);
        tv_paraNameShow.setText(paraNameShow);
        tv_startTime.setText(startTime);
        tv_dealer.setText(dealer);
        tv_content.setText(content);
        tv_dealTime.setText(dealTime);
        tempFileName=tempFile.getAbsolutePath();
        init();
        tv_contentcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AlarmDealActivity.this);
                builder.setMessage(content);
                builder.setTitle("处理内容详情");
                builder.create().show();
            }
        });
        tv_choicepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        IMAGE_UNSPECIFIED);
                startActivityForResult(intent1, TOGALLERY);*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        startPermissionsActivity();
                    } else {
                        selectFromAlbum();
                    }
                } else {
                    selectFromAlbum();
                }
                isClickCamera = false;






            }
        });
        tv_getpicture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                //startCamera();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        startPermissionsActivity();
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
                isClickCamera = true;


            }
        });
    }



    //5.11
    private void init() {
        mPermissionsChecker = new PermissionsChecker(this);
    }


    /**
     * 打开系统相机
     */
    private void openCamera() {
        File file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageUri = FileProvider.getUriForFile(this, "sany.com.mmpapp.provider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 从相册选择
     */
    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    /**
     * 裁剪
     */
    private void cropPhoto() {
        File file = new FileStorage().createCropFile();
        outputUri = Uri.fromFile(file);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_PICTURE_CUT);
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION,
                PERMISSIONS);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        if(data==null) {
            return;
        }else {
            imagePath = null;
            imageUri = data.getData();
            if (DocumentsContract.isDocumentUri(this, imageUri)) {
                //如果是document类型的uri,则通过document id处理
                String docId = DocumentsContract.getDocumentId(imageUri);
                if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                    String id = docId.split(":")[1];//解析出数字格式的id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
                //如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(imageUri, null);
            } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
                //如果是file类型的Uri,直接获取图片路径即可
                imagePath = imageUri.getPath();
            }

            cropPhoto();
        }
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
        cropPhoto();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE://从相册选择
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == RESULT_OK) {
                    cropPhoto();
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                Bitmap bitmap = null;
                try {
                    if (isClickCamera) {

                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } else {
                        // bitmap = BitmapFactory.decodeFile(imagePath);
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    }
                    iv_picturedisplay.setImageBitmap(bitmap);
                    try {
                        saveFile(bitmap,tempFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
                } else {
                    if (isClickCamera) {
                        openCamera();
                    } else {
                        selectFromAlbum();
                    }
                }
                break;
        }
    }





    public void startCamera(){
        ActivityCompat.requestPermissions(AlarmDealActivity.this,
                new String[]{android.Manifest.permission.CAMERA}, 1);
        Intent intent0 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

               /* intent0.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent0, TOCAMERA);*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion<24){
            if (!tempFile.exists()) {
                try {
                    tempFile.getParentFile().mkdirs();
                    tempFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            intent0.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(intent0, TOCAMERA);
        }else {
            ActivityCompat.requestPermissions(AlarmDealActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            if (!tempFile.exists()) {
                try {
                    tempFile.getParentFile().mkdirs();
                    tempFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
             uriContext=FileProvider.getUriForFile(this,"sany.com.mmpapp.provider", tempFile);
            //ContentValues contentValues =new ContentValues(1);
            //contentValues.put(MediaStore.Images.Media.DATA,uriContext);
            //Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            intent0.putExtra(MediaStore.EXTRA_OUTPUT, uriContext);
            startActivityForResult(intent0, TOCAMERA);
        }

    }

    //@Override
   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Bitmap photo;
        switch (requestCode) {
            case TOGALLERY:
                if(data==null){
                    return;
                }
                Uri originalUri = data.getData();
                if (originalUri == null) {
                    return;
                }
                imageUri=cutPictureUtils.crop(originalUri);
                break;
            case TOCROP:
                if(imageUri != null){
                    Bitmap bitmap = cutPictureUtils.decodeUriAsBitmap(imageUri);
                    iv_picturedisplay.setImageBitmap(bitmap);
                    try {
                        saveFile(bitmap,tempFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TOCAMERA:
                File imgUri = new File(GetImagePath.getPath(getBaseContext(), data.getData()));
               // imageUri=cutPictureUtils.crop(Uri.fromFile(tempFile));
               Uri uri= FileProvider.getUriForFile(this,"sany.com.mmpapp.provider", imgUri);
                //Uri inputUri = FileProvider.getUriForFile(this, "sany.com.mmpapp.provider", tempFile);
                //imageUri=cutPictureUtils.crop(inputUri);
                imageUri=cutPictureUtils.crop(uri);
                Log.i("imageUri",imageUri.toString());

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private Bitmap getBitpMap(Uri uri){
        try {
            Bitmap bitmap=BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void saveFile(Bitmap bm, String fileName) throws IOException {
        if (!tempFile.exists()) {
            try {
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 10, bos);
        bos.flush();
        bos.close();
    }

    public void saveFile(){
        if (!tempFile.exists()) {
            try {
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






    public void submitpicture(){
        String end ="\r\n";
        String twoHyphens ="--";
        String boundary =java.util.UUID.randomUUID().toString();
        try
        {
            URL url =new URL("http://mmp.sany.com.cn:8010/mobile/uploadPicture.do");
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
          /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout(5000);
          /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            con.setRequestProperty("Charset","utf-8");
          /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            StringBuffer sb = new StringBuffer();
            sb.append(twoHyphens);
            sb.append(boundary);
            sb.append(end);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"uploadDraw\"; filename=\"" + tempFile.getName() + "\"" + end);
            sb.append("Content-Type: application/octet-stream; charset=" + "utf-8" + end);
            sb.append(end);
            FileInputStream fStream =new FileInputStream(tempFileName);
            int fileLen=fStream.available();
          /* 设置DataOutputStream */
            DataOutputStream ds =new DataOutputStream(con.getOutputStream());
            ds.write(sb.toString().getBytes());
            String fileName=tempFile.getName();
          /* 取得文件的FileInputStream */
          /* 设置每次写入1024bytes */
            int bufferSize =2048;
            byte[] buffer =new byte[bufferSize];
            int length =-1;
          /* 从文件读取数据至缓冲区 */
            while((length = fStream.read(buffer)) !=-1)
            {
            /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* close streams */
            fStream.close();
            ds.flush();
          /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) !=-1 )
            {
                b.append( (char)ch );
            }
            ds.close();
          /* 将Response显示于Dialog */
           // showDialog("上传成功"+b.toString().trim());
          /* 关闭DataOutputStream */
            String tempStr=b.toString().trim();
            jsonRep=new JSONObject(tempStr);
            alarmDealHandler.sendEmptyMessage(Http.SIG_GOOD);

        }
        catch(Exception e)
        {
            e.printStackTrace();
            showDialog("上传失败");
        }
    }
    /* 显示Dialog的method */
    private void showDialog(String mess)
    {
        new AlertDialog.Builder(AlarmDealActivity.this).setTitle("Message")
                .setMessage(mess)
                .setNegativeButton("确定",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }

    Handler alarmDealHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        String pictureName= jsonRep.getString("rows");
                        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
                        String dateStr=df.format(new Date());
                        String lawerContent=et_lawer_content.getText().toString();
                        String UserCode=mmpApp.getUserCode();
                        insertAlarmDealRec(alarmid,lawerContent,UserCode,dateStr,pictureName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_REC:
                    try {
                        String temp= jsonRep.getString("rows");
                        if(temp!="0"){
                            Toast.makeText(AlarmDealActivity.this,"上传成功",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case Http.SIG_BAD:
                    break;
                case Http.SIG_BAD_JSON:
                    break;
                case Http.ILL_URL:
                    break;
                case Http.HOST_TIME_OUT:
                    break;
                case Http.OK_HOST:
                    break;
                default:
                    break;
            }
        }

    };
    public void insertAlarmDealRec(int alarmId,String dealContent,String dealPerson,String dealTime,String pictureName){
        String url = APIInterface.DATAHOST + APIInterface.updateAlarmrecAndAlarmdealrec;
        AlarmDealThread alarmDealThread = new AlarmDealThread(alarmId, dealContent, dealPerson, dealTime, pictureName,url);
        new Thread(alarmDealThread).start();
    }

    class AlarmDealThread implements Runnable {
        private int alarmId;
        private String dealContent;
        private String dealPerson;
        private String dealTime;
        private String pictureName;
        private String fullUrl;

        /**
         * @param url 地址
         */
        public AlarmDealThread(int alarmId,String dealContent,String dealPerson,String dealTime,String pictureName, String url) {
            // TODO Auto-generated constructor stub
            this.alarmId=alarmId;
            this.dealContent=dealContent;
            this.dealPerson=dealPerson;
            this.dealTime=dealTime;
            this.pictureName=pictureName;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair paramalarmId = new BasicNameValuePair(
                    "alarmId", ""+alarmId);
            paraLists.add(paramalarmId);
            BasicNameValuePair paramdealContent = new BasicNameValuePair(
                    "dealContent", ""+dealContent);
            paraLists.add(paramdealContent);
            BasicNameValuePair paramdealPerson = new BasicNameValuePair(
                    "dealPerson", ""+dealPerson);
            paraLists.add(paramdealPerson);
            BasicNameValuePair paramdealTime = new BasicNameValuePair(
                    "dealTime", ""+dealTime);
            paraLists.add(paramdealTime);
            BasicNameValuePair parampictureName = new BasicNameValuePair(
                    "pictureName", ""+pictureName);
            paraLists.add(parampictureName);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                alarmDealHandler.sendEmptyMessage(SIG_GOOD_REC);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                alarmDealHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                alarmDealHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                alarmDealHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}

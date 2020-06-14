package com.ak47.digiboard.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ak47.digiboard.R;
import com.ak47.digiboard.adapter.ExaminerQuestionListAdapter;
import com.ak47.digiboard.model.QuestionListModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victor.loading.rotate.RotateLoading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ExaminerNewQuestionsFromFile extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "QuestionListFormFile";
    public String actualFilePath = "";
    private int request_code = 1, FILE_SELECT_CODE = 101;
    private RecyclerView recyclerView;
    private FloatingActionButton saveButton;
    private Button selectFile;
    private ExaminerQuestionListAdapter adapter;
    private ArrayList<QuestionListModel> questionList;
    private String quizName, quizDescription;
    private RotateLoading rotateLoading;
    private TextView noQuestionFoundTextView;
    private CardView sampleInfoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examiner_new_questions_from_file);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(R.string.quiz_preview);

        rotateLoading = findViewById(R.id.mainLoading);
        noQuestionFoundTextView = findViewById(R.id.no_question_found);

        Intent intent = getIntent();
        if (intent != null) {
            quizName = intent.getStringExtra("quizName");
            quizDescription = intent.getStringExtra("quizDescription");
        }

        saveButton = findViewById(R.id.saveQuestionButton);
        selectFile = findViewById(R.id.selectFile);
        sampleInfoCard = findViewById(R.id.sampleFileCardView);
        saveButton.setOnClickListener(this);
        selectFile.setOnClickListener(this);

        recyclerView = findViewById(R.id.questionListId);
        questionList = new ArrayList<>();
        adapter = new ExaminerQuestionListAdapter(getApplicationContext(), questionList, saveButton, noQuestionFoundTextView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExaminerNewQuestionsFromFile.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.selectFile:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // start runtime permission
                    boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request_code);
                    } else {
                        showFileChooser();
                    }
                } else {
                    showFileChooser();
                }
                sampleInfoCard.setVisibility(View.GONE);
                noQuestionFoundTextView.setVisibility(View.VISIBLE);
                break;
            case R.id.saveQuestionButton:
                // call SaveQuiz class Constructor
                if (questionList.size() > 5) {
                    rotateLoading.start();
                    examinerSaveQuizList(questionList, quizName, quizDescription);

                } else {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage("Number of Question Must Be More then 5")
                            .show();
                }
                break;
        }
    }

    private void examinerSaveQuizList(ArrayList<QuestionListModel> questionList, String quizName, String quizDescription) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AdminUsers").child(userId).child("MyQuizLists");

        HashMap<String, ArrayList<QuestionListModel>> questionListMap = new HashMap<>();
        questionListMap.put("questionList", questionList);

        String key = rootRef.push().getKey();
        rootRef.child(key).setValue(questionListMap);
        rootRef.child(key).child("quizName").setValue(quizName);
        rootRef.child(key).child("quizDescription").setValue(quizDescription);
        rootRef.child(key).child("createdDateTime").setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime()));
        rootRef.child(key).child("publishInfo").setValue(false).addOnCompleteListener(task -> {
            rotateLoading.stop();
            sendToMainActivity();

        });
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(ExaminerNewQuestionsFromFile.this, ExaminerMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Questions are not saved")
                .setMessage("Are you really want to close ?")
                .setPositiveButton("Yes", (dialog, which) -> sendToMainActivity())
                .setNeutralButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileChooser();
            } else {
                new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                        .setTitle("Alert")
                        .setMessage("Please Grant Permission")
                        .show();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (Exception e) {
            Log.e(TAG, " Choose File Error " + e.toString());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData();
                    InputStream stream = null;
                    String tempID, id;
                    Uri uri = data.getData();
                    Log.d(TAG, "file auth is " + uri.getAuthority());
                    switch (imageUri.getAuthority()) {
                        case "media": {
                            tempID = imageUri.toString();
                            tempID = tempID.substring(tempID.lastIndexOf("/") + 1);
                            id = tempID;
                            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            String selector = MediaStore.Images.Media._ID + "=?";
                            actualFilePath = getColumnData(contentUri, selector, new String[]{id});
                            break;
                        }
                        case "com.android.providers.media.documents": {
                            tempID = DocumentsContract.getDocumentId(imageUri);
                            String[] split = tempID.split(":");
                            String type = split[0];
                            id = split[1];
                            Uri contenturi = null;
                            switch (type) {
                                case "image":
                                    contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    break;
                                case "video":
                                    contenturi = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                    break;
                                case "audio":
                                    contenturi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                    break;
                            }
                            String selector = "_id=?";
                            actualFilePath = getColumnData(contenturi, selector, new String[]{id});
                            break;
                        }
                        case "com.android.providers.downloads.documents": {
                            tempID = imageUri.toString();
                            tempID = tempID.substring(tempID.lastIndexOf("/") + 1);
                            id = tempID;
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                            // String selector = MediaStore.Images.Media._ID+"=?";
                            actualFilePath = getColumnData(contentUri, null, null);
                            break;
                        }
                        case "com.android.externalstorage.documents": {
                            tempID = DocumentsContract.getDocumentId(imageUri);
                            String[] split = tempID.split(":");
                            String type = split[0];
                            id = split[1];
                            if (type.equals("primary")) {
                                actualFilePath = Environment.getExternalStorageDirectory() + "/" + id;
                            }
                            break;
                        }
                    }
                    File myFile = new File(actualFilePath);
                    String temppath = uri.getPath();
                    if (temppath.contains("//")) {
                        temppath = temppath.substring(temppath.indexOf("//") + 1);
                    }
                    if (actualFilePath.equals("") || actualFilePath.equals(" ")) {
                        myFile = new File(temppath);
                    } else {
                        myFile = new File(actualFilePath);
                    }
                    readfile(myFile);
                } catch (Exception e) {
                    Log.e(TAG, " read Error " + e.toString());
                }
            }
        }
    }

    public String getColumnData(Uri uri, String selection, String[] selectArg) {
        String filepath = "";
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};
        cursor = getContentResolver().query(uri, projection, selection, selectArg, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Log.d(TAG, " file path is " + cursor.getString(cursor.getColumnIndex(column)));
            filepath = cursor.getString(cursor.getColumnIndex(column));
        }
        if (cursor != null)
            cursor.close();
        return filepath;
    }

    public void readfile(File file) {
        String question = null, optionA = null, optionB = null, optionC = null, optionD = null;
        int answer;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            int count = 1;
            String line;
            while ((line = br.readLine()) != null) {

                if (count == 1) {
                    question = line;
                    Log.e("a :", question);
                    count++;
                } else if (count == 2) {
                    optionA = line;
                    Log.e("b: ", optionA);
                    count++;
                } else if (count == 3) {
                    optionB = line;
                    Log.e("c: ", optionB);
                    count++;
                } else if (count == 4) {
                    optionC = line;
                    Log.e("d: ", optionC);
                    count++;
                } else if (count == 5) {
                    optionD = line;
                    Log.e("e: ", optionD);
                    count++;
                } else if (count == 6) {
                    answer = Integer.parseInt(line);
                    Log.e("e: ", String.valueOf(answer));
                    count = 1;


                    if (question == null && optionA == null && optionB == null && optionC == null && optionD == null && (answer != 1 || answer != 2 || answer != 3 || answer != 4)) {
                        warning();
                    } else {
                        QuestionListModel model = new QuestionListModel(question, optionA, optionB, optionC, optionD, answer);
                        questionList.add(model);
                        Log.e(TAG, "list " + questionList);
                        adapter.notifyDataSetChanged();
                        saveButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    warning();
                }
            }
            br.close();
        } catch (Exception e) {
            Log.e(TAG, " error is " + e.toString());
        } finally {
            if (questionList.size() > 0) {
                selectFile.setVisibility(View.GONE);
            }
        }

    }

    public void warning() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Please Recheck File")
                .setMessage("File is not in specified format")
                .setPositiveButton("Ok", (dialog, which) -> sendToMainActivity())
                .show();
    }

}
package com.hk.read.ocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hk.read.R;
import com.hk.read.base.BaseActivity;
import com.hk.read.ocr.entity.OcrImg;
import com.hk.read.ocr.entity.PageInput;
import com.hk.read.ocr.imp.IOcrPresenter;
import com.hk.read.ocr.imp.IOcrView;
import com.hk.read.utils.FileUtil;

import java.util.List;


public class OCRActivity extends BaseActivity implements IOcrView, View.OnClickListener {

    private Button startCan;
    private Button wordExplain;
    private Button send;
    private TextView logView;

    private IOcrPresenter presenter;
    private String mPath;
    private Button mergeWord;
    private Button clearData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        initView();
        presenter = new OcrPresenter(this);
    }

    private void initView() {
        startCan = (Button) findViewById(R.id.start_scan);
        wordExplain = (Button) findViewById(R.id.word_explain);
        send = (Button) findViewById(R.id.send);
        logView = (TextView) findViewById(R.id.out_log);
        mergeWord = (Button) findViewById(R.id.merge_word);
        clearData = (Button) findViewById(R.id.clear_data);

        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        startCan.setOnClickListener(this);
        wordExplain.setOnClickListener(this);
        send.setOnClickListener(this);
        mergeWord.setOnClickListener(this);
        clearData.setOnClickListener(this);
    }

    @Override
    public void updateLog(String msg) {
//        Editable text = (Editable) logView.getText();
//        text.append(msg);
//        logView.setText(text);

        setText2Log(msg);
    }

    private void setText2Log(String msg) {
        logView.append(msg);
        int offset = logView.getLineCount() * logView.getLineHeight();
        if (offset > logView.getHeight()) {
            logView.scrollTo(0, offset - logView.getHeight());
        }
    }

    @Override
    public void updateBtnStatu(int statu) {
        wordExplain.setText("文字解析");
        wordExplain.setEnabled(true);
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_scan:
                //启动扫描
                showEt(new InputListener() {
                    @Override
                    public void input(PageInput page) {
                        try {
                            int num = Integer.parseInt(page.arg1);
                            presenter.startWordRecgnize(num);
                        } catch (Exception e) {
                            Toast.makeText(OCRActivity.this, "请输入数字", Toast.LENGTH_LONG).show();
                            updateLog("\n输入页码不合法");
                        }

                    }
                });

                break;
            case R.id.word_explain:
                //解析文字
                if (TextUtils.isEmpty(mPath)) {
                    updateLog("\n还未拍照，请拍照后重试");
                    return;
                }

                wordExplain.setText("正在解析中...");
                wordExplain.setEnabled(false);
                presenter.handleResult(new OcrImg(mPath));


                break;
            case R.id.merge_word:
                showMergeEt();
                break;

            case R.id.clear_data:
                showClearDialog();
                break;
            case R.id.send:
                //发送文字
                showFileList();
                break;
            default:
        }
    }
    //单选选中的文件
    int choice = 0;
    private void showFileList() {
         List<String> files = FileUtil.getAllSaveTxtFile(this);
        //list转为数组
        final String[] strings = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            strings[i] = files.get(i);
        }
        new AlertDialog.Builder(this)
                .setTitle("选择发送的文件")
                .setIcon(R.drawable.icon)
                //默认选中了哪些，点击也不会自动关闭
                .setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice = which;
                      updateLog("\n选中了"+strings[which]);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateLog("\n正在发送" + strings[choice]);
                        presenter.sendTxt(strings[choice]);
                    }
                })
                .setNegativeButton("取消",null)
                .create().show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OcrPresenter.START_CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //处理成功
                mPath = data.getStringExtra("filePath");
                updateLog("临时图片存放路径：" + mPath);

            }

//                presenter.handleResult();
        } else {
            updateLog("拍照失败，请重试");
        }
    }


    private void showEt(final InputListener listener) {
        final EditText et = new EditText(this);
        et.setHint("输入要扫描页码");
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this).setTitle("请输入页码")
                .setIcon(R.drawable.icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            updateLog("\n输入的页码为空");
                            Toast.makeText(getApplicationContext(), "搜索内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            updateLog("\n输入的页码:" + input);
                            listener.input(new PageInput(input));
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();


    }

    private void showMergeEt() {
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setWeightSum(2);

        final EditText startEt = new EditText(this);
        startEt.setHint("输入要合并的起始页");
        startEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText endEt = new EditText(this);
        endEt.setHint("输入要合并的结束页（包含）");
        endEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.removeAllViews();
        layout.addView(startEt);
        layout.addView(endEt);
        new AlertDialog.Builder(this).setTitle("请输入页码")
                .setIcon(R.drawable.icon)
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String startInput = startEt.getText().toString();
                        String endInput = endEt.getText().toString();
                        if (startInput.equals("") || endInput.equals("")) {
                            updateLog("\n输入的页码为空");
                            Toast.makeText(getApplicationContext(), "搜索内容不能为空！" + startInput, Toast.LENGTH_LONG).show();
                        } else {
                            updateLog("\n输入的页码:" + startInput + "—" + endInput);
                            try {
                                int start = Integer.parseInt(startInput);
                                int end = Integer.parseInt(endInput);
                                presenter.mergePage(start, end);
                            } catch (Exception e) {
                                Toast.makeText(OCRActivity.this, "请输入数字", Toast.LENGTH_LONG).show();
                                updateLog("\n输入页码不合法");
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();


    }

    private void showClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle("警告")
                .setMessage("清理数据，是将以前的拍照和解析的缓存文件全部删除。清理后，无法复原。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateLog("\n确认清理。");
                        boolean b = presenter.clearWord();
                        updateLog(b ? "\n清理成功" : "\n清理失败");
                    }
                })
                .setNegativeButton("取消", null);
        builder.show();
    }

    interface InputListener {
        void input(PageInput page);
    }
}


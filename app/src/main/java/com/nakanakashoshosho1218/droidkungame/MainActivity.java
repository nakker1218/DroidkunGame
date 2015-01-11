package com.nakanakashoshosho1218.droidkungame;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    TextView scoreText;
    TextView timeLabel;
    TextView timeText;
    TextView gameStartLabel;
    TextView gameOverLabel;

    ImageView[] imageView = new ImageView[25];

    int score;
    int rnd;


    int width;
    int high;

    int color;
    int baseColor;

    float baseHue;
    float baseSaturation;
    float baseValue;

    float[] colorValue = {0.7f, 0.75f, 0.8f, 0.85f, 0.9f, 0.95f};

    boolean isMissTouch;
    int countTimes;

    CountDownTimer mCountDownTimer;
    long time = 15000;

    private AnimationSet mTouchAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isMissTouch = false;
        countTimes = 0;

        scoreText = (TextView) findViewById(R.id.textView_score);
        timeLabel = (TextView) findViewById(R.id.label_time);
        timeText = (TextView) findViewById(R.id.textView_time);
        gameStartLabel = (TextView) findViewById(R.id.label_gameStart);
        gameOverLabel = (TextView) findViewById(R.id.label_gameOver);

        //TextViewのフォント(DIN Condensed Bold)
        Typeface typeface = Typeface.createFromAsset(getAssets(), "DIN Condensed Bold.ttf");
        scoreText.setTypeface(typeface);
        timeLabel.setTypeface(typeface);
        timeText.setTypeface(typeface);
        gameStartLabel.setTypeface(typeface);
        gameOverLabel.setTypeface(typeface);

        //ImageViewの関連付け
        for (int i = 0; i < imageView.length; i++) {
            String ivId = "imageView" + (i + 1);
            int resID = getResources().getIdentifier(ivId, "id", "com.nakayamashohei.droidkungame");

            imageView[i] = (ImageView) findViewById(resID);
            imageView[i].setImageResource(R.drawable.droid);
            imageView[i].setTag(new Integer(i));

            imageView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = ((Integer) v.getTag()).intValue();
                    if (index == rnd) {
                        imageView[index].startAnimation(mTouchAnimation);
                        imageView[index].setBackgroundColor(Color.parseColor("#E91E63"));
                        imageView[index].setEnabled(false);
                        score++;
                        scoreText.setText(String.valueOf(score));

                        if (isMissTouch){
                            isMissTouch = false;
                            Log.d("MainActivity", "isMissTouch");
                        }
                        countTimes++;

                        if (countTimes >= 3){
                            Log.d("MainActivity", "countTimes" + countTimes);

                            time = time + 3000;
                            mCountDownTimer.cancel();
                            timer();
                            countTimes = 0;
                        }
                    } else {
                        isMissTouch = true;
                        countTimes = 0;
                    }
                }
            });
        }

        //スタート画面のタッチのイベントを全部吸収
        findViewById(R.id.startLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //スコア
        score = 0;
        scoreText.setText(String.valueOf(score));

        random();
        question();

        //画面サイズの取得
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //タップ時のアニメーション
        width = size.x / 5;
        high = width;

        mTouchAnimation = new AnimationSet(true);
        ScaleAnimation touchStartAnim = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, width / 2, high / 2);
        touchStartAnim.setDuration(500);
        mTouchAnimation.addAnimation(touchStartAnim);
        ScaleAnimation touchEndAnim = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, width / 2, high / 2);
        touchEndAnim.setDuration(300);
        mTouchAnimation.addAnimation(touchEndAnim);
        mTouchAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                question();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    //タイマー
    public void timer() {

        mCountDownTimer = new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                StringBuilder builder = new StringBuilder();
                //second
                builder.append(String.format("%1$02d", (int) (millisUntilFinished / 1000)));
                builder.append("'");
                //milli second
                builder.append(String.format("%1$02d", (int) ((millisUntilFinished % 1000) / 10)));

                time = millisUntilFinished;

                timeText.setText(builder.toString());

            }

            @Override
            public void onFinish() {
                timeText.setText("00'00");

                //Finish時
                findViewById(R.id.finishLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.finishLayout).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //タッチのイベントを全部吸収
                        return true;
                    }
                });
            }
        }.start();
    }

    //問題の表示
    public void question() {
        random();
        for (int i = 0; i < imageView.length; i++) {
//            imageView[i].setBackgroundColor(baseColor);

            imageView[i].setBackgroundColor(baseColor);
            imageView[i].setEnabled(true);
        }
        imageView[rnd].setBackgroundColor(color);
    }

    //色の調整
    public void random() {
        rnd = (int) (Math.random() * 25);

        baseHue = (float) (Math.random() * 349) + 1;
        baseSaturation = (float) (Math.random() * 0.5f) + 0.5f;

        if (score <= 5) {
            baseValue = colorValue[0];
        } else if (score <= 10) {
            baseValue = colorValue[1];
        } else if (score <= 15) {
            baseValue = colorValue[2];
        }

        float[] baseHsv = {baseHue, baseSaturation, baseValue};
        float[] hsv = {baseHue, baseSaturation, 1f};

        baseColor = Color.HSVToColor(baseHsv);
        color = Color.HSVToColor(hsv);
    }

    //Start画面にてStartボタンが押された時の処理
    public void start(View v) {
        findViewById(R.id.startLayout).setVisibility(View.GONE);
        timer();
    }

    //リスタート時の処理
    public void restart(View v) {
        findViewById(R.id.finishLayout).setVisibility(View.GONE);
        score = 0;
        scoreText.setText(String.valueOf(score));
        time = 15000;
        random();
        question();
        timer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
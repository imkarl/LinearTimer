package cn.jeesoft.lineartimer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mShowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowText = (TextView) findViewById(R.id.show_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LinearTimer.isStart()) {
                    LinearTimer.stop();
                    mShowText.append("stop ing...");
                } else {
                    LinearTimer.start();
                    mShowText.setText("start ing...\n");
                    LinearTimer.addTimeListener(System.currentTimeMillis()+2*60*1000, new OnLinearTimerListener() {
                        @Override
                        public void onProgress(long time, int count) {
                            mShowText.append("time="+time+", count="+count+"\n");
                        }
                        @Override
                        public void onStop(long time, int count) {
                            mShowText.append("onStop time="+time+", count="+count);
                        }
                        @Override
                        public void onFinish(long time) {
                            mShowText.append("onFinish time="+time);
                        }
                    });
                }
            }
        });
    }

}

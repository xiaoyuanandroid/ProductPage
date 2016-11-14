package com.xiaoyuan;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, StickyScrollView.OnScrollChangedListener {

    TextView oneTextView, twoTextView;
    private StickyScrollView stickyScrollView;
    private int height;
    private LinearLayout llContent;
    private RelativeLayout llTitle;
    private FrameLayout frameLayout;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListeners();

    }

    /**
     * 初始化View
     */
    private void initView() {
        stickyScrollView = (StickyScrollView) findViewById(R.id.scrollView);
        frameLayout = (FrameLayout) findViewById(R.id.tabMainContainer);
        title = (TextView) findViewById(R.id.title);
        oneTextView = (TextView) findViewById(R.id.infoText);
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        llTitle = (RelativeLayout) findViewById(R.id.ll_good_detail);
        oneTextView.setOnClickListener(this);
        twoTextView = (TextView) findViewById(R.id.secondText);
        twoTextView.setOnClickListener(this);

        stickyScrollView.setOnScrollListener(this);
        StatusBarUtil.setTranslucentForImageView(MainActivity.this, 0, title);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) llTitle.getLayoutParams();
        params.setMargins(0, getStatusHeight(), 0, 0);
        llTitle.setLayoutParams(params);

        //默认设置一个Frg
        getSupportFragmentManager().beginTransaction().replace(R.id.tabMainContainer, Fragment.newInstance()).commit();
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusHeight() {
        int resourceId = MainActivity.this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.infoText) {
            getSupportFragmentManager().beginTransaction().replace(R.id.tabMainContainer, Fragment.newInstance()).commit();
        } else if (v.getId() == R.id.secondText) {
            getSupportFragmentManager().beginTransaction().replace(R.id.tabMainContainer, Fragment1.newInstance()).commit();

        }
    }


    private void initListeners() {
        //获取内容总高度
        final ViewTreeObserver vto = llContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = llContent.getHeight();
                //注意要移除
                llContent.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);

            }
        });

        //获取Fragment高度
        ViewTreeObserver viewTreeObserver = frameLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = height - frameLayout.getHeight();
                //注意要移除
                frameLayout.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });

        //获取title高度
        ViewTreeObserver viewTreeObserver1 = llTitle.getViewTreeObserver();
        viewTreeObserver1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = height - llTitle.getHeight() - getStatusHeight();//计算滑动的总距离
                stickyScrollView.setStickTop(llTitle.getHeight() + getStatusHeight());//设置距离多少悬浮
                //注意要移除
                llTitle.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });


    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t <= 0) {
            llTitle.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
            StatusBarUtil.setTranslucentForImageView(MainActivity.this, 0, title);
        } else if (t > 0 && t <= height) {
            float scale = (float) t / height;
            int alpha = (int) (255 * scale);
            llTitle.setBackgroundColor(Color.argb((int) alpha, 227, 29, 26));//设置标题栏的透明度及颜色
            StatusBarUtil.setTranslucentForImageView(MainActivity.this, alpha, title);//设置状态栏的透明度
        } else {
            llTitle.setBackgroundColor(Color.argb((int) 255, 227, 29, 26));
            StatusBarUtil.setTranslucentForImageView(MainActivity.this, 255, title);
        }
    }
}

package com.bzu.yhd.pocketcampus.widget;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bzu.yhd.pocketcampus.R;

/**
 * 作者：漆可 on 2016/8/31 13:53
 */
public class TransitionView extends RelativeLayout
{

    private View v_spread; // 播放扩散动画的view
    private View v_line;
    private TextView tv_sign_up;
    private TextView tv_success;
    private TextView tv_error;
    private OnAnimationEndListener mOnAnimationEndListener;
    private String sore=null;
    public TransitionView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TransitionView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        //允许绘制背景，及执行onDraw()方法
//        setWillNotDraw(false);

        init();
    }

    private void init()
    {
        View mRootView = inflate(getContext(), R.layout.view_transtion, this);

        v_spread = mRootView.findViewById(R.id.v_spread);
        v_line = mRootView.findViewById(R.id.v_line);
        tv_sign_up = (TextView) mRootView.findViewById(R.id.tv_sign_up);
        tv_success = (TextView) mRootView.findViewById(R.id.tv_success);
        tv_error = (TextView) mRootView.findViewById(R.id.tv_error);
    }

    /**
     * 开始播放动画
     */
    public void startSuccessAnimation()
    {
        this.setVisibility(View.VISIBLE);

        tv_sign_up.setTranslationX(0);
        tv_sign_up.setVisibility(View.INVISIBLE);
        tv_success.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.GONE);
        v_line.setVisibility(View.INVISIBLE);

        //缩放动画
        AnimationHelper.spreadAni(v_spread, getScale(), new AnimationHelper.SimpleAnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                sore="success";
                //结束后播放sign up 文字显示入场动画
                startSignUpInAni(sore);
            }
        });
    }
    /**
     * 开始播放动画
     */
    public void startErrorAnimation()
    {
        this.setVisibility(View.VISIBLE);

        tv_sign_up.setTranslationX(0);
        tv_sign_up.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.INVISIBLE);
        tv_success.setVisibility(View.GONE);
        v_line.setVisibility(View.INVISIBLE);

        //缩放动画
        AnimationHelper.spreadAni(v_spread, getScale(), new AnimationHelper.SimpleAnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                sore="error";
                //结束后播放sign up 文字显示入场动画
                startSignUpInAni(sore);
            }
        });
    }


    private void startSignUpInAni(final String sore)
    {
        AnimationHelper.signUpTextInAni(tv_sign_up, new AnimationHelper.SimpleAnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                //开启线条播放动画
                startLineAni(sore);
            }
        });
    }

    private void startLineAni(final String sore)
    {
        AnimationHelper.lineExpendAni(v_line, new AnimationHelper.SimpleAnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if(sore!=null)
                //开启success文字入场动画
                {
                    if(sore.equals("success"))
                        startSuccessAni();
                    else if(sore.equals("error"))
                    {
                        startErrorAni();
                    }

                }
            }
        });
    }

    private void startSuccessAni()
    {
        AnimationHelper.successInAni(tv_success, tv_sign_up, new AnimationHelper.SimpleAnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                //回调
                if (mOnAnimationEndListener != null)
                {
                    mOnAnimationEndListener.onEnd();
                }
            }
        });
    }
    private void startErrorAni()
    {
        AnimationHelper.successInAni(tv_error, tv_sign_up, new AnimationHelper.SimpleAnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                //回调
                if (mOnAnimationEndListener != null)
                {
                    mOnAnimationEndListener.onEnd();
                }
            }
        });
    }

    //计算扩散动画最终放大比例
    private float getScale()
    {
        //原始扩散圆的直径
        int orgWidth = v_spread.getMeasuredWidth();

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //扩散圆最终扩散的圆的半径
        float finalDiameter = (int) (Math.sqrt(width * width + height * height));

        //因为圆未居中，所以加1
        return finalDiameter / orgWidth + 1;
    }

    public void setOnAnimationEndListener(OnAnimationEndListener onAnimationEndListener)
    {
        this.mOnAnimationEndListener = onAnimationEndListener;
    }

    /**
     * 动画结束监听
     */
    public interface OnAnimationEndListener
    {
        void onEnd();
    }
}

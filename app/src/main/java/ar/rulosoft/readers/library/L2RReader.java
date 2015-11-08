package ar.rulosoft.readers.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Raul on 25/10/2015.
 */
public class L2RReader extends R2LReader {

    boolean firstTime = true;
    float totalWidth = 0;

    public L2RReader(Context context) {
        super(context);
    }

    public L2RReader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public L2RReader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void relativeScroll(double distanceX, double distanceY) {
        if (XScroll + distanceX > (((totalWidth * mScaleFactor) - screenWidth)) / mScaleFactor) {
            XScroll = ((totalWidth * mScaleFactor) - screenWidth) / mScaleFactor;
            stopAnimationOnHorizontalOver = true;
        } else if (XScroll + distanceX > 0) {
            XScroll += distanceX;
        } else {
            XScroll = 0;
            stopAnimationOnHorizontalOver = true;
        }
        if (YScroll + distanceY > (((screenHeight * mScaleFactor) - screenHeight)) / mScaleFactor) {
            YScroll = ((screenHeight * mScaleFactor) - screenHeight) / mScaleFactor;
            stopAnimationOnVerticalOver = true;
        } else if (YScroll + distanceY < 0) {
            YScroll = 0;
        } else {
            YScroll += distanceY;
            stopAnimationOnVerticalOver = true;
        }
    }

    @Override
    public void absoluteScroll(float x, float y) {
        if (x > (((totalWidth * mScaleFactor) - screenWidth)) / mScaleFactor) {
            XScroll = ((totalWidth * mScaleFactor) - screenWidth) / mScaleFactor;
            stopAnimationOnHorizontalOver = true;
        } else if (x > 0) {
            XScroll = x;
        } else {
            XScroll = 0;
            stopAnimationOnHorizontalOver = true;
        }
        if (y > (((screenHeight * mScaleFactor) - screenHeight)) / mScaleFactor) {
            YScroll = ((screenHeight * mScaleFactor) - screenHeight) / mScaleFactor;
            stopAnimationOnVerticalOver = true;
        } else if (y < 0) {
            YScroll = 0;
        } else {
            YScroll = y;
            stopAnimationOnVerticalOver = true;
        }
    }

    @Override
    protected void calculateVisibilities() {
        float scrollXAd = getPagePosition(currentPage);
        float acc = 0;
        for (int i = pages.size() - 1; i >= 0; i--) {
            Page d = pages.get(i);
            d.init_visibility = (float) Math.floor(acc);
            acc += d.scaled_width;
            acc = (float) Math.floor(acc);
            d.end_visibility = acc;
        }
        scrollXAd = getPagePosition(currentPage) - scrollXAd;//correction for new added pages
        totalWidth = acc;
        if (firstTime) {
            XScroll = getPagePosition(0);
            firstTime = false;
        } else {
            relativeScroll(scrollXAd, 0);
        }
        pagesLoaded = true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY) {
        if (mOnEndFlingListener != null && e2.getX() - e1.getX() > 100 && (XScroll == 0)) {
            mOnEndFlingListener.onEndFling();
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    /*
     * Starting from 0
    */
    @Override
    public float getPagePosition(int page) {
        if (page < 0) {
            return pages.get(0).init_visibility;
        } else if (page < pages.size())
            return pages.get(page).end_visibility - screenWidth - 2;
        else
            return pages.get(pages.size() - 1).init_visibility;
    }
}
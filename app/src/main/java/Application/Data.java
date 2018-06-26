package Application;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Administrator on 2018/6/11.
 */

public class Data extends Application{

    private static Data instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

    private WindowManager.LayoutParams mlayoutParams = null;

    private WindowManager mwindowManager;

    private View mfloatView;

    public WindowManager.LayoutParams getMlayoutParams() {
        return mlayoutParams;
    }

    public void setMlayoutParams(WindowManager.LayoutParams mlayoutParams) {
        this.mlayoutParams = mlayoutParams;
    }

    public WindowManager getMwindowManager() {
        return mwindowManager;
    }

    public void setMwindowManager(WindowManager mwindowManager) {
        this.mwindowManager = mwindowManager;
    }
    public View getMfloatView() {
        return mfloatView;
    }

    public void setMfloatView(View mfloatView) {
        this.mfloatView = mfloatView;
    }

    public static Context getMyApplication() {
        return instance;
    }
}


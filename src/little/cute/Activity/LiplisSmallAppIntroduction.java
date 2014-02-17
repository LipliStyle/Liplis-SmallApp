//=======================================================================
//  ClassName : LiplisSmallAppIntroduction
//  概要      : リプリススモールアプリ紹介画面
//
//	extends   : Activity
//	impliments:
//
//	Liplisの紹介画面
//
//  LiplisAndroidシステム
//  Copyright(c) 2011-2011 sachin. All Rights Reserved.
//=======================================================================
package little.cute.Activity;

import little.cute.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class LiplisSmallAppIntroduction extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//親クラスの
        super.onCreate(savedInstanceState);

        //レイアウトのセット
        setContentView(R.layout.introduction_smapp);

        //クローズボタンのセット
        setCloseButton();

        //設定ボタン呼び出しイベントのセット
        setSettingButton();
    }

    /// <summary>
    /// setSettingButton
    /// 設定ボタンのハンドらセット
    /// </summary>
    private void setSettingButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnIntroSetting);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppSelecter.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setCloseButton
    /// クローズボタンのハンドらセット
    /// </summary>
    private void setCloseButton()
    {
        //ボタンの配置
        Button closeButton = (Button) findViewById(R.id.btnIntroClose);

        //クリックリスナーの作成
        closeButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				finish();
		    }
		});
    }



}

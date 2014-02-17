//=======================================================================
//  ClassName : LiplisSmallAppSelecter
//  概要      : リプリススモールアプリセレクタ
//
//	extends   : LiplisWidgeSelecter
//	impliments:
//
//	Liplisの操作画面
//
//  LiplisAndroidシステム
//  Copyright(c) 2011-2013 sachin. All Rights Reserved.
//=======================================================================
package little.cute.Activity;

import little.cute.R;
import little.cute.Common.LiplisDefine;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LiplisSmallAppSelecter extends LiplisWidgeSelecter{
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//親クラスの
        super.onCreate(savedInstanceState);
    }

    /// <summary>
    /// setNextButton
    /// ネクストボタンのハンドらセット
    /// </summary>
    @Override
    public void setNextButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnSelectNext);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(LiplisDefine.LIPLIS_CLICK_ACTION);
		    	sendBroadcast(intent);
		    	finish();
		    }
		});
    }

    /// <summary>
    /// setSleepButton
    /// スリープボタンのハンドらセット
    /// </summary>
    @Override
    protected void setSleepButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnSelectSleep);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(LiplisDefine.LIPLIS_CLICK_ACTION_SLEEP);
		    	sendBroadcast(intent);
		    	finish();
		    }
		});
    }

    /// <summary>
    /// setBatteryButton
    /// 電池ボタンのハンドらセット
    /// </summary>
    @Override
    protected void setBatteryButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnSelectBattery);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(LiplisDefine.LIPLIS_CLICK_ACTION_BATTERY);
		    	sendBroadcast(intent);
		    	finish();
		    }
		});
    }


    /// <summary>
    /// setClockButton
    /// 時計ボタンのハンドらセット
    /// </summary>
    @Override
    protected void setClockButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnSelectClock);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(LiplisDefine.LIPLIS_CLICK_ACTION_CLOCK);
		    	sendBroadcast(intent);
		    	finish();
		    }
		});
    }

    /// <summary>
    /// setSettingButton
    /// 設定ボタンのハンドらセット
    /// </summary>
    @Override
    protected void setSettingButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnSelectSetting);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfiguration.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setSettingTopicButton
    /// 話題設定ボタンのハンドらセット
    /// </summary>
    @Override
    protected void setSettingTopicButton()
    {
        //ボタンの配置
        Button btn = (Button) findViewById(R.id.btnSelectTopic);

        //クリックリスナーの作成
        btn.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfigurationTopic.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setSettingVoiceButton
    /// ボイス設定ボタンのハンドらセット
    /// </summary>
    @Override
    protected void setSettingVoiceButton()
    {
        //ボタンの配置
        Button btn = (Button) findViewById(R.id.btnSelectVoice);

        //クリックリスナーの作成
        btn.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfigurationVoice.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

  /// <summary>
    /// setSettingTwitterButton
    /// ツイッター登録ボタンのハンドらセット
    /// </summary>
    protected void setSettingTwitterButton()
    {
        //ボタンの配置
        Button btn = (Button) findViewById(R.id.btnConfigTopicTwitter);

        //クリックリスナーの作成
        btn.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfigurationTwitter.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }


    /// <summary>
    /// setSettingSearchSetting
    /// 検索設定登録ボタンのハンドらセット
    /// </summary>
    protected void setSettingSearchSetting()
    {
        //ボタンの配置
        Button btn = (Button) findViewById(R.id.btnNewsFilterSetting);

        //クリックリスナーの作成
        btn.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfigurationSearchSettingRegist.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setSettingRssButton
    /// RSS_URL登録画面
    /// </summary>
    protected void setSettingRssButton()
    {
    	//ボタンの配置
        Button button = (Button) findViewById(R.id.btnRssUrlRegister);

        //クリックリスナーの作成
        button.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfigurationRssUrlRegist.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setIntroButton
    /// 紹介ボタンのハンドらセット
    /// </summary>
    @Override
    protected void setIntroButton()
    {
        //ボタンの配置
        Button settingButton = (Button) findViewById(R.id.btnSelectIntro);

        //クリックリスナーの作成
        settingButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppIntroduction.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }


    /// <summary>
    /// setSiteButton
    /// サイトボタンのハンドらセット
    /// </summary>
    @Override
    protected void setSiteButton()
    {
        //ボタンの配置
        Button closeButton = (Button) findViewById(R.id.btnCtlSite);

        //クリックリスナーの作成
        closeButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent("android.intent.action.VIEW", Uri.parse(LiplisDefine.SITE_MAIN));
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setHelpButton
    /// ヘルプボタンのハンドらセット
    /// </summary>
    @Override
    protected void setHelpButton()
    {
        //ボタンの配置
        Button closeButton = (Button) findViewById(R.id.btnCtlHelp);

        //クリックリスナーの作成
        closeButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent("android.intent.action.VIEW", Uri.parse(LiplisDefine.SITE_HELP));
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setMarketButton
    /// マーケットボタンのハンドらセット
    /// </summary>
    @Override
    protected void setMarketButton()
    {
        //ボタンの配置
        Button closeButton = (Button) findViewById(R.id.btnMarket);

        //クリックリスナーの作成
        closeButton.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent("android.intent.action.VIEW", Uri.parse(LiplisDefine.SITE_MARKET_SMALL));
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setVersion
    /// バージョンを設定する
    /// </summary>
    protected void setVersion()
    {
    	TextView txtVer = (TextView) findViewById(R.id.txtVersion);
    	PackageInfo packageInfo = null;

    	try {
    	        packageInfo = getPackageManager().getPackageInfo("little.cute.Smallapp", PackageManager.GET_META_DATA);
    	        txtVer.setText("バージョン:" + packageInfo.versionName);
    	} catch (NameNotFoundException e) {
    	        e.printStackTrace();
    	        txtVer.setText("バージョン:?");
    	}
    }
}

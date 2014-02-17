//=======================================================================
//  ClassName : LiplisSmallAppConfigurationTwitter
//  概要      : スモールアプリの設定画面
//
//	extends   : LiplisWidgetConfigurationTwitter
//	impliments:
//
//	レイアウトファイル : twitterconfiguration.xml
//
//  プリファレンスには変換値ではなく、設定値をのものを入力する!
//
//2013/03/27 ver3.3.6 Twitter対応
//
//  LiplisAndroidシステム
//  Copyright(c) 2011-2013 sachin. All Rights Reserved.
//=======================================================================
package little.cute.Activity;

import little.cute.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("WorldWriteableFiles")
public class LiplisSmallAppConfigurationTwitter extends LiplisWidgetConfigurationTwitter {

    ///====================================================================
    ///
    ///                             初期化処理
    ///
    ///====================================================================

    /// <summary>
    /// onCreate
    /// 作成時処理
    /// </summary>
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//親クラスの
        super.onCreate(savedInstanceState);
    }

  /// <summary>
    /// setTwitterPassCodeButton
    /// ツイッター登録ボタンのハンドらセット
    /// </summary>
    protected void setTwitterPassCodeButton()
    {
        //ボタンの配置
        Button button = (Button) findViewById(R.id.btnSetTwitterPassCode);

        //クリックリスナーの作成
        button.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisWidgetConfigurationTwitterRegist.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }

    /// <summary>
    /// setTwitterUserButton
    /// ツイッターユーザー登録画面
    /// </summary>
    protected void setTwitterUserButton()
    {
    	//ボタンの配置
        Button button = (Button) findViewById(R.id.btnConfigTopicTwitterUser);

        //クリックリスナーの作成
        button.setOnClickListener(new OnClickListener() {
		  	//クリックイベント
			public void onClick(View v) {
				if(requestToken.equals("") || accessToken.equals(""))
				{
					viewResult("ツイッターの認証登録を行なってからボタンを押してください。");
					return;
				}

				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfigurationTwitterUserRegist.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
		    }
		});
    }
}

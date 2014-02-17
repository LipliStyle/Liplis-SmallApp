//=======================================================================
//  ClassName : MainApplication
//  概要      : リプリスモールアプリ
//
//	extends   : SmallApplication
//	impliments:
//
//  LiplisAndroid SmallApp
//  Copyright(c) 2013-2013 sachin.
//=======================================================================

package little.cute.Smallapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import little.cute.Activity.LiplisSmallAppConfiguration;
import little.cute.Activity.LiplisSmallAppLog;
import little.cute.Activity.LiplisSmallAppSelecter;
import little.cute.Common.LiplisDefine;
import little.cute.Common.LiplisUtil;
import little.cute.Fct.FctLiplisMsg;
import little.cute.Msg.MsgShortNews;
import little.cute.Obj.ObjBattery;
import little.cute.Obj.ObjBody;
import little.cute.Obj.ObjClock;
import little.cute.Obj.ObjLiplisBody;
import little.cute.Obj.ObjLiplisChat;
import little.cute.Obj.ObjLiplisLogList;
import little.cute.Obj.ObjPreference;
import little.cute.Obj.ObjR;
import little.cute.Ser.SerialLiplisNews;
import little.cute.Web.LiplisNews;
import little.cute.Xml.LiplisChatSetting;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AnalogClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallApplication;

public class MainApplication extends SmallApplication  implements TextToSpeech.OnInitListener{

	///=====================================
    /// クラス
	protected static LiplisNews lpsNews;						//リプリスニュース
	protected static ObjR or;									//アールオブジェクト
	protected static ObjLiplisBody olb;						//リプリスボディオブジェクト
	protected static ObjBody ob;								//現在表示ボディオブジェクト
	protected static ObjBattery obt;							//バッテリーオブジェクト
	protected static ObjLiplisChat olc;						//チャットオブジェクト
	protected static ObjPreference op;						//プリファレンスオブジェクト
	protected static ObjLiplisLogList ol;						//ログオブジェクト
	protected static ObjClock oc;								//時間オブジェクト
	protected static TextToSpeech tts;						//TTSオブジェクト  2013/05/01 ver3.4.0 おしゃべり対応

	///=====================================
    /// ダウンロード用スレッド
	protected Thread thread;

	///=====================================
    /// 設定値
	//NOTE : liplisRefreshRate * liplisRate = 更新間隔 (updateCntに関連)
	protected static int liplisInterval = 10;			//インターバル
	protected static int liplisRefresh	= 10;			//リフレッシュレート
	protected static int liplisWindowCode = 0;			//ウインドウコード

	///=====================================
    /// チャット制御カウント
	protected static int cntUpdate = 10;

	///=====================================
    /// 制御フラグ
	//protected static int		flgSize = 0;						//サイズ
	protected static int 	 flgAlarm = 0;						//アラームフラグ
	protected static boolean flgThinking     	= false;		//考え中
	protected static boolean flgClockMode 		= false;		//クロックモード
	protected static boolean flgChatting 		= false;		//チャット中
	protected static boolean flgSkip 			= false;		//スキップ中
	protected static boolean flgSitdown 			= false;		//おやすみステータス
	protected static boolean flgAutoSleepOn 		= false;		//自動スリープがONになったときON
	protected static boolean flgIconOn 			= true;			//アイコンオン
	protected static boolean flgWindowOn 		= true;			//ウインドウオン	ver3.0
	protected static boolean flgGettingTopic 	= false;		//トピックの取得中

    ///=====================================
    /// 表示制御カウンタ
	protected static int cntBlink 	= 0;       				//1回/5～10s
	protected static int cntMouth 	= 0;       				//1回/1s

    ///=====================================
    /// 制御プロパティ
    protected static MsgShortNews liplisNowTopic;
    protected static MsgShortNews liplisPrvTopic;
    protected static String liplisNowWord 	= "";			//現在読み込みの単語(cntLnwでカウント)
    protected static String liplisChatText 	= "";			//現在読み込みの文字(cntLctでカウント)
    protected static int cntLct				= 0;			//リプリスチャットテキストカウント
    protected static int cntLnw				= 0;			//リプリスナウワードカウント
    protected static int nowPoint 			= 0;			//現在感情ポイント
    protected static int nowEmotion 		= 0;				//感情現在値
    protected static int prvEmotion 		= 0;				//感情前回値
    protected static String prvNewsFlg 		= "";			//前回ニュースフラグ

    ///=====================================
    /// 音声制御プロパティ  2013/05/01 ver3.4.0 おしゃべり対応
    protected float pitch = 1.0f;								//音声制御ピッチ
    protected float rate = 1.0f;								//音声制御レート

    ///=====================================
    /// アイコン制御
    protected static int cntIconClose = 0;					//アイコン消去カウント
	protected static int batteryNowId = 0;					//バッテリーID

	///=====================================
    /// エラー表示用
	protected static final String errorClassName ="AppWidgetProvider:";

	///=============================
    /// コントロール
	protected TextView txtLiplisTalkText;
	protected ImageView ivLiplisWindow;
	protected ImageView ivLiplisBody;
	protected ImageView ivLiplisSleep;
	protected ImageView ivLiplisLog;
	protected ImageView ivLplisSetting;
	protected ImageView ivLiplisThinking;
	protected AnalogClock ivLiplisAngClock;
	protected ImageView ivLiplisBattery;

	protected ImageView ivImYear1;
	protected ImageView ivImYear2;
	protected ImageView ivImYear3;
	protected ImageView ivImYear4;
	protected ImageView ivImMonth1;
	protected ImageView ivImMonth2;
	protected ImageView ivImDay1;
	protected ImageView ivImDay2;
	protected ImageView ivImHour1;
	protected ImageView ivImHour2;
	protected ImageView ivImMin1;
	protected ImageView ivImMin2;

	protected ImageView ivImBattery01;
	protected ImageView ivImBattery02;
	protected ImageView ivImBattery03;
	protected ImageView ivImBattery04;
	protected ImageView ivImBattery05;
	protected ImageView ivImBattery06;
	protected ImageView ivImBattery07;
	protected ImageView ivImBattery08;
	protected ImageView ivImBattery09;
	protected ImageView ivImBattery10;

	protected LinearLayout llLpsSleep;
	protected LinearLayout llLpsLog;
	protected LinearLayout llLpsSetting;
	protected LinearLayout llLpsAngClock;
	protected LinearLayout llLpsbattery;
	protected LinearLayout llClockAndBattery;
	protected LinearLayout llTalkText;


    ///====================================================================
    ///
    ///                           イベントハンドラ
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : base
    /// MethodName : onCreate
    /// 作成時
    /// </summary>
	@Override
    public void onCreate() {
        super.onCreate();

        //スモールAPPの初期化
        initSmallApp();

        //コントロールの初期化
        initControll();

        //リスナーの初期化
        initListener();

        //イベントハンドラの初期化
        initEventHandler();

        //リプリスの初期化
        initLiplis();

        //オブジェクトの初期化
        initObject();

        //タイマーの初期化
        startHandler();

        flgAlarm = 11;
    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onUpdate
    /// 100秒おきに呼ばれる。
    /// </summary>
    private void onUpdate()
    {
    	//
    	if(flgAlarm == 12)
    	{
    		updateLiplis();
    	}
    	else if(flgAlarm == 10)
    	{
    		//カウントダウンフェーズ
    		onCountDown();
    	}
    	else if(flgAlarm == 11)
    	{
    		//話題取得フェーズ
    		nextLiplis();
    		onCheckNewsQueue();
    	}
    	else
    	{

    	}
    }

    @Override
    public void onStart() {
        super.onStart();
        initLiplis();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        flgAlarm =0;

        shudDonwnTts();
    }

    ///====================================================================
    ///
    ///                     ブロードキャストレシーバー
    ///
    ///====================================================================

    // スレッドの通知を受けるためのレシーバ

    /// <summary>
    //  MethodType : base
    /// MethodName : mReceiver
    /// ブロードキャストレシーバー
    /// インテント受信の許可は「initListener」メソッドで行う。
    /// </summary>
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	try
        	{
            	//引数のインテントからアクションを取得
                final String action = intent.getAction();

                //バッテリーチェンジリスナー
                if(Intent.ACTION_BATTERY_CHANGED.equals(action))
                {
                	onChangeBattery(intent);
                }
                //クリックリスナー
                else if(LiplisDefine.LIPLIS_CLICK_ACTION.equals(action))
                {
                	onClickBody();
                }
                //スリープリスナー
                else if(LiplisDefine.LIPLIS_CLICK_ACTION_SLEEP.equals(action))
                {
                	onClickIconSleep();
                }
                //スクリーンオンリスナー
                else if(Intent.ACTION_SCREEN_ON.equals(action))
        		{
                	onWaikup();
        		}
                //スクリーンオフリスナー
                else if(Intent.ACTION_SCREEN_OFF.equals(action))
                {
                	onSleep();
                }
                //バッテリークリックリスナー
                else if(LiplisDefine.LIPLIS_CLICK_ACTION_BATTERY.equals(action))
                {
                	onClickBattery();
                }
                //クロッククリックリスナー
                else if(LiplisDefine.LIPLIS_CLICK_ACTION_CLOCK.equals(action))
                {
                	onClickClock();
                }
                //設定クリックリスナー
                else if(LiplisDefine.LIPLIS_SETTING_START.equals(action))
                {
                	onSettingStart();

                }
                //設定完了リスナー
                else if(LiplisDefine.LIPLIS_SETTING_FINISH.equals(action))
                {
                	onReciveSettingChange();
                }
                //システム情報が変更された
                else if(Intent.ACTION_CONFIGURATION_CHANGED.equals(action))
                {
                	onConfigurationChenge();
                }
                else if(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(action))
                {
                }
                else if(AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action))
                {
                }
                else
                {

                }
        	}
        	catch(Exception e)
        	{
        		Log.d("LiplisSmallApp br","error");
        	}
        }
    };

    ///====================================================================
    ///
    ///                       非オーバーライドリスナー
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : base
    /// MethodName : onChangeBattery
    /// バッテリー変更イベント
    /// 2011/07/25 var1.2.0
    /// </summary>
    public void onChangeBattery(Intent intent)
    {
    	updateBatteryInfo(intent);
    }

    /// <summary>
    /// onCountDown
    /// カウントダウンイベント
    /// </summary>
    protected void onCountDown()
    {
        try
        {
    		//カウントダウン
    		cntUpdate--;

    		//チャットフェーズに以降
    		if(cntUpdate <= 0)
    		{
    			flgAlarm = 11;
    		}

    		//ウインドウオフチェック
    		// ウインドウ消去がON かつ ウインドウフラグがON かつ LPSMODEが4以外の場合、10秒以上経過しているかチェック
    		if(op.getLpsWindowDis() == 1 && flgWindowOn && op.getLpsMode() != 4)
    		{
    			//10秒経過していたらウインドウを閉じる
    			if(liplisInterval - cntUpdate > 10)
        		{
        			windowOff();
        		}
    		}


    	}
    	catch(Exception e)
    	{
    	}

    }

    /// <summary>
    /// onCheckNewsQueue
    /// ニュースキューチェック
    //2013/03/01 ver3.2.0 LiplisNewsに非同期でデータ収集
    /// </summary>
    protected void onCheckNewsQueue()
    {
        try
        {
        	//時計モード時は実行しない
        	if(op.getLpsMode() == 4)
        	{
        		return;
        	}

        	//2013/03/03 ver3.3.1 ジャンルの更新チェック
        	if(prvNewsFlg.equals(op.getTopicFlg()))
        	{
        		//キューの補充チェック
        		lpsNews.checkNewsQueue(this,getNameValuePairForLiplisNewsList());
        	}
        	else
        	{

        		//前回ニュースフラグの更新
        		prvNewsFlg = op.getTopicFlg();

        		//キューのリフレッシュ
        		lpsNews.refleshNewsQueue(this,getNameValuePairForLiplisNewsList());
        	}


    	}
    	catch(Exception e)
    	{
    	}

    }

    /// <summary>
    /// onClickBody
    /// クリックイベント
    /// </summary>
    public void onClickBody()
    {
        try
    	{
        	//時計モード時無効
        	if(op.getLpsMode() == 4)
        	{
        		oc.updateClockObject();
        		iconOn();
        		nextClick();
        		return;
        	}

        	//チャット中チェック
        	if(!flgChatting)
        	{
        		onCheckNewsQueue();
        		iconOn();
        		nextClick();
        	}
        	else
        	{
        		flgSkip = true;
        	}
    	}
    	catch(Exception e)
    	{
    	}

    }



    /// <summary>
    //  MethodType : base
    /// MethodName : onWaikup
    /// 起床
    /// 2011/07/31 var1.2.0
    /// </summary>
    public void onWaikup()
    {
        try
    	{
        	//時計モード時無効
        	if(op.getLpsMode() == 4)
        	{
        		runClock();
        		return;
        	}

        	if(op.getLpsAutoWaikup() == 1)
        	{
        		standUp();
        	}
    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onSleep
    /// おやすみ
    /// 2011/07/31 var1.2.0
    /// </summary>
    protected void onSleep()
    {
        try
    	{
        	//時計モード時無効
        	if(op.getLpsMode() == 4){return;}

        	if(op.getLpsAutoSleep() == 1)
        	{
        		flgAutoSleepOn = true;
        		sitDown();
        	}

			if (tts.isSpeaking()) {
				// 読み上げ中なら止める
				tts.stop();
			}
    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onChangeBattery
    /// スリープアイコンクリック
    /// 2011/08/25 var2.0.0
    /// </summary>
    public void onClickIconSleep()
    {
        try
    	{
        	//時計モード時無効
        	if(op.getLpsMode() == 4){return;}

        	if(flgSitdown)
        	{
        		standUp();
        	}
        	else
        	{
        		sitDown();
        	}
    	}
    	catch(Exception e)
    	{
    	}


    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onClickBattery
    /// バッテリーアイコンクリック
    /// 2011/08/25 var2.0.0
    /// </summary>
    public void onClickBattery()
    {
        try
    	{
        	//時計モード時無効
        	if(op.getLpsMode() == 4){return;}

        	batteryInfo();

    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onClickClock
    /// 時計アイコンクリック
    /// 2011/08/25 var2.0.0
    /// </summary>
    public void onClickClock()
    {
        try
    	{
        	//時計モード時無効
        	if(op.getLpsMode() == 4){return;}

        	clockInfo();
    	}
    	catch(Exception e)
    	{
    	}
    }

  /// <summary>
    //  MethodType : base
    /// MethodName : onSettingStart
    /// 設定開始
    /// 2011/08/25 var2.0.0
    /// </summary
    public void onSettingStart()
    {
    	try
     	{
    		chatStop();
    		flgAlarm = 0;
     	}
     	catch(Exception e)
     	{
     	}
    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onReciveSettingChange
    /// 設定変更通知
    /// 2011/08/25 var2.0.0
    /// </summary
    public void onReciveSettingChange()
    {
    	try
     	{
    		//設定の読み込み
    		loadSetting();

    		//スイッチする
    		switchTalkClock(op.getLpsMode() == 4);

    		//フラグを
    		flgAlarm = 11;
     	}
     	catch(Exception e)
     	{
     	}
    }

    /// <summary>
    //  MethodType : base
    /// MethodName : onConfigurationChenge
    /// システム情報変更イベント
    /// 2011/08/25 var2.0.0
    /// </summary
    @SuppressWarnings("deprecation")
	public void onConfigurationChenge()
    {
		//アラームを初期に設定
		flgAlarm = 0;

    	try
     	{
    		switch (this.getResources().getConfiguration().orientation){
    	    case Configuration.ORIENTATION_LANDSCAPE:;
    	     break;
    	    case Configuration.ORIENTATION_PORTRAIT:
    	     break;
    	    case Configuration.ORIENTATION_SQUARE:
    	     break;
    	    default:
    	     break;
    	   }
     	}
     	catch(Exception e)
     	{
     	}
    }



    ///====================================================================
    ///
    ///                            タイマー
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : base
    /// MethodName : -
    /// ハンドラーを定期実行する設定。
    /// 以下のとおり記述し、runを実行すると、メソッド内で設定したインターバルで
    /// 繰り返し実行される。
    /// </summary>
	Handler handler = new Handler();
	boolean running = true;
	Runnable runnable = new Runnable() {
		public void run() {
			if (running) {
				onUpdate();
			}
			handler.postDelayed(runnable, LiplisDefine.LIPLIS_SMALL_UPDATE_RATE);
		}
	};

    /// <summary>
    //  MethodType : child
    /// MethodName : startHandler
    /// ハンドラーの初期化、起動
    /// </summary>
    private void startHandler()
    {
    	runnable.run();
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : stopHandler
    /// ハンドラー停止
    /// </summary>
    private void stopHandler()
    {
    	 handler.removeCallbacks(runnable);
    	 handler=null;
    }

    ///====================================================================
    ///
    ///                           副イベント
    ///
    ///====================================================================


    /// <summary>
    //  MethodType : child
    /// MethodName : sidDown
    /// すわり
    /// </summary>
    protected boolean sitDown()
    {
    	try
    	{
    		//すわり有効、おしゃべり中ならおしゃべりメソッド内で処理
    		flgSitdown = true;

    		//おしゃべり中でなければ座りモーション
    		//おしゃべり中はれフレッシュメソッド内で処理
    		if(!flgChatting)
    		{
    			//座りモーション
    			updateBodySitDown();
    		}

    		//アイコン変更
    		updateSleepIcon();

    		//2013/05/02 ver3.4.1 お休み時自動復帰防止対応
    		//シットダウン状態に移行する
    		op.updateLpsStuts(this,1);

    		return true;
		}
		catch(Exception e)
		{
			return false;
		}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : standUp
    /// 立ち上がり
    /// </summary>
    protected boolean standUp()
    {
    	try
    	{
    		//2013/05/02 ver3.4.1 お休み時自動復帰防止対応
    		//シットダウン状態に移行する
    		op.updateLpsStuts(this,0);

    		flgSitdown = false;

    		//アイコン変更
    		updateSleepIcon();

    		//あいさつ
    		greet();

    		return true;
		}
		catch(Exception e)
		{
			return false;
		}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : iconOn
    /// アイコンオン
    /// </summary>
    protected boolean iconOn()
    {
        try
    	{
        	flgIconOn = true;
        	cntIconClose = op.getLpsIconCloseCnt() * 10;	//LiplisSmallの場合、レートを1/10で動作させているので、インターバルを10倍する

            ivLiplisSleep.setVisibility(View.VISIBLE);
            ivLiplisLog.setVisibility(View.VISIBLE);
            ivLplisSetting.setVisibility(View.VISIBLE);
            ivLiplisThinking.setVisibility(View.VISIBLE);
            ivLiplisAngClock.setVisibility(View.VISIBLE);
            ivLiplisBattery.setVisibility(View.VISIBLE);
        	return true;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : iconOff
    /// アイコンオフ
    /// </summary>
    protected boolean iconOff()
    {
        try
    	{
        	flgIconOn = false;
        	cntIconClose = -1;

            ivLiplisSleep.setVisibility(View.GONE);
            ivLiplisLog.setVisibility(View.GONE);
            ivLplisSetting.setVisibility(View.GONE);
            ivLiplisThinking.setVisibility(View.GONE);
            ivLiplisAngClock.setVisibility(View.GONE);
            ivLiplisBattery.setVisibility(View.GONE);

        	return true;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : windowOn
    /// ウインドウオン
    /// ver3.0 2012/03/31
    /// </summary>
    protected boolean windowOn()
    {
        try
    	{
        	flgWindowOn = true;

            ivLiplisWindow.setVisibility(View.VISIBLE);
            txtLiplisTalkText.setVisibility(View.VISIBLE);

        	return true;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : windowOff
    /// ウインドウオン
    /// ver3.0 2012/03/31
    /// </summary>
    protected boolean windowOff()
    {
        try
    	{
        	flgWindowOn = false;

            //ウインドウ消去
            txtLiplisTalkText.setText("");
            txtLiplisTalkText.setVisibility(View.GONE);
            ivLiplisWindow.setVisibility(View.GONE);

        	return true;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}
    }

    ///====================================================================
    ///
    ///                         イベントリスナー
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : child
    /// MethodName : setPhoneStateListner
    /// 着信時イベントの実装
    /// 以下のパーミッションが必要
    /// <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    ///
    /// </summary>
    protected boolean setPhoneStateListner(Class<?> cls)
	{
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

            // リスナーの作成
            PhoneStateListener listener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: //待ち受け状態

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: //通話開始時
                        //stateString = "通話開始 "+ incomingNumber;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING: //着信時
                        //stateString = "着信中"+ incomingNumber;
                        break;
                    }

                }
            };

            // リスナーの登録
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        	return true;
        }
        catch(Exception e)
        {
        	return false;
        }
	}


    /// <summary>
    /// setIvLiplisBodyListener
    /// リプリスボディのハンドらセット
    /// </summary>
    private void setIvLiplisBodyListener()
    {
        //クリックリスナーの作成
        ivLiplisBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickBody();
			}
        });
    }

    /// <summary>
    /// setIvLiplisBodyListener
    /// リプリスボディのハンドらセット
    /// </summary>
    private void setIvLiplisWindow()
    {
        //クリックリスナーの作成
    	ivLiplisWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(op.getLpsWindowClick() == 2)
	        	{
					if(liplisPrvTopic != null)
					{
						Intent intent=new Intent("android.intent.action.VIEW", Uri.parse(liplisPrvTopic.url));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(intent);
					}

	        	}
	        	else if(op.getLpsWindowClick() == 1)
	        	{
	        		Intent intent=new Intent(getApplicationContext(),LiplisSmallAppConfiguration.class);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            startActivity(intent);
	        	}
			}
        });
    }

    /// <summary>
    /// setLlLpsLog
    /// リプリスログのハンドらセット
    /// </summary>
    private void setLlLpsLog()
    {
        //クリックリスナーの作成
    	llLpsLog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppLog.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            intent.putExtra("LOGLIST", ol);	//ログデータの引き渡し
	            startActivity(intent);
			}
        });
    }

    /// <summary>
    /// setLlLpsSetting
    /// リプリス設定のハンドらセット
    /// </summary>
    private void setLlLpsSetting()
    {
        //クリックリスナーの作成
    	llLpsSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),LiplisSmallAppSelecter.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent);
			}
        });
    }

    /// <summary>
    /// setLlLpsSleep
    /// スリープボタンのハンドらセット
    /// </summary>
    private void setLlLpsSleep()
    {
        //クリックリスナーの作成
    	llLpsSleep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickIconSleep();
			}
        });
    }

    /// <summary>
    /// setLlLpsAngClock
    /// アナログクロックボタンのハンドらセット
    /// </summary>
    private void setLlLpsAngClock()
    {
        //クリックリスナーの作成
    	llLpsAngClock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickClock();
			}
        });
    }

    /// <summary>
    /// setLlLpsbattery
    /// バッテリーボタンのハンドらセット
    /// </summary>
    private void setLlLpsbattery()
    {
        //クリックリスナーの作成
    	llLpsbattery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickBattery();
			}
        });
    }

    ///====================================================================
    ///
    ///                           実装メソッド
    ///
    ///====================================================================

    protected RemoteViews getRemoteViews(String packageName)
    {
    	return new RemoteViews(packageName, or.layout);
    }


    ///====================================================================
    ///
    ///                           初期化処理
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : child
    /// MethodName : initSmallApp
    /// スモールアプリの初期化
    /// </summary>
    protected void initSmallApp()
    {
    	//アールオブジェクト
    	or = new ObjR(getFlgSize());

        //レイアウト読み込み
        //サイズ変更する場合、選択させてからこの処理をすれば良いと思われる。
        setContentView(R.layout.widget);

        //アプリタイトル設定
        setTitle(R.string.app_name);

        //ウインドウ属性の取得
        SmallAppWindow.Attributes attr = getWindow().getAttributes();

        //幅設定
        attr.width = getResources().getDimensionPixelSize(R.dimen.width);
        //高さ設定
        attr.height = getResources().getDimensionPixelSize(R.dimen.height);

        //サイズ変更許可
        attr.flags |= SmallAppWindow.Attributes.FLAG_RESIZABLE;

        //タイトルバー除去
        attr.flags |= SmallAppWindow.Attributes.FLAG_NO_TITLEBAR;

        //ハードウェアアクセれレーションレンダリングオン
        attr.flags |= SmallAppWindow.Attributes.FLAG_HARDWARE_ACCELERATED;

        //設定の反映
        getWindow().setAttributes(attr);
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : initControll
    /// コントロールの初期化
    /// </summary>
    protected void initControll()
    {
    	//テキストビューの初期化
    	txtLiplisTalkText = (TextView)findViewById(or.liplisTalkText);

        //イメージビューの初期化
    	ivLiplisWindow = (ImageView) findViewById(or.liplisWindow);
    	ivLiplisBody = (ImageView) findViewById(or.liplisImage);
    	ivLiplisSleep= (ImageView) findViewById(or.liplisSleep);
    	ivLiplisLog= (ImageView) findViewById(or.liplisLog);
    	ivLplisSetting= (ImageView) findViewById(or.liplisSetting);
    	ivLiplisThinking= (ImageView) findViewById(or.liplisThinking);
    	ivLiplisAngClock= (AnalogClock) findViewById(or.liplisAngClock);
    	ivLiplisBattery= (ImageView) findViewById(or.liplisBattery);
    	llClockAndBattery= (LinearLayout) findViewById(or.llClockAndBattery);
    	llTalkText= (LinearLayout) findViewById(or.llTalkText);
    	llLpsLog = (LinearLayout) findViewById(or.llLpsLog);
    	llLpsSetting = (LinearLayout) findViewById(or.llLpsSetting);
    	llLpsSleep = (LinearLayout) findViewById(or.llLpsSleep);
    	llLpsAngClock = (LinearLayout) findViewById(or.llLpsAngClock);
    	llLpsbattery = (LinearLayout) findViewById(or.llLpsbattery);

    	ivImYear1 = (ImageView) findViewById(or.imYear1);
		ivImYear2 = (ImageView) findViewById(or.imYear2);
		ivImYear3 = (ImageView) findViewById(or.imYear3);
		ivImYear4 = (ImageView) findViewById(or.imYear4);
		ivImMonth1 = (ImageView) findViewById(or.imMonth1);
		ivImMonth2 = (ImageView) findViewById(or.imMonth2);
		ivImDay1 = (ImageView) findViewById(or.imDay1);
		ivImDay2 = (ImageView) findViewById(or.imDay2);
		ivImHour1 = (ImageView) findViewById(or.imHour1);
		ivImHour2 = (ImageView) findViewById(or.imHour2);
		ivImMin1 = (ImageView) findViewById(or.imMin1);
		ivImMin2 = (ImageView) findViewById(or.imMin2);

		ivImBattery01 = (ImageView) findViewById(or.imBattery01);
		ivImBattery02 = (ImageView) findViewById(or.imBattery02);
		ivImBattery03 = (ImageView) findViewById(or.imBattery03);
		ivImBattery04 = (ImageView) findViewById(or.imBattery04);
		ivImBattery05 = (ImageView) findViewById(or.imBattery05);
		ivImBattery06 = (ImageView) findViewById(or.imBattery06);
		ivImBattery07 = (ImageView) findViewById(or.imBattery07);
		ivImBattery08 = (ImageView) findViewById(or.imBattery08);
		ivImBattery09 = (ImageView) findViewById(or.imBattery09);
		ivImBattery10 = (ImageView) findViewById(or.imBattery10);

    }



    /// <summary>
    //  MethodType : child
    /// MethodName : initLiplis
    /// 初期化
    /// </summary>
    protected void initObject()
    {
    	//2013/03/01 ver3.1.0 LiplisNewsの初期化
		lpsNews = SerialLiplisNews.loadObject(this);

    	//プリファレンスオブジェクト
    	op = new ObjPreference(this);

    	//OLBの初期化
    	olb = new ObjLiplisBody();

    	//バッテリーインフォの初期化
    	obt = new ObjBattery();

    	//OLBよりデフォルトのたち絵データをロードしておく
    	ob = olb.getDefaultBody();

    	//ログオブジェクトの初期化
    	ol = new ObjLiplisLogList();

    	//時間オブジェクトの初期化
    	oc = new ObjClock();

    	//チャット設定の取得
		olc = new LiplisChatSetting().getChatSetting(this.getResources().getXml(R.xml.chat));
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : initLiplis
    /// 初期化
    /// </summary>
    protected void initLiplis()
    {
    	try
    	{
        	//まずアラームフラグを0に退避
        	flgAlarm = 0;

        	//クロックモード
        	flgClockMode = false;

        	//おしゃべり初期化
        	flgChatting = false;

        	//
        	//起こしておく		2012/10/29 ver3.0
        	flgSitdown = false;

        	//設定のよみこみ
        	loadSetting();

        	//音声オブジェクトの初期化 2013/05/01 ver3.4.0 おしゃべり対応
            initTts();

        	//2013/03/03 ver3.3.1 ニュース設定変更時にキューをリフレッシュするように考慮
        	prvNewsFlg = op.getNewsFlg();

        	//バッテリー表示
        	updateBatteryInfo(null);

        	//チャットインフォの初期化
        	initChatInfo();

        	//2013/05/02 ver3.4.1 お休み時自動復帰防止対応 standUpでグリーとさせる
        	//2013/06/28 ver3.4.2 再配置時にろーでぃんぐなうで止まる問題への対応(refleshOnlyメソッド追加)
        	if(op.getLpsStatus() == 0)
        	{
        		refleshOnly();
        		standUp();
        	}
        	else
        	{
        		sitDown();
        		refleshOnly();
        		updateBodySitDown();
        	}

    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : initListener
    /// リスナーの初期化
    /// </summary>
    protected void initListener()
    {
    	//リスナーの追加
    	createIntentFilter(Intent.ACTION_BATTERY_CHANGED);
    	createIntentFilter(Intent.ACTION_SCREEN_ON);
    	createIntentFilter(Intent.ACTION_SCREEN_OFF);
    	createIntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
    	createIntentFilter(LiplisDefine.LIPLIS_CLICK_ACTION);
    	createIntentFilter(LiplisDefine.LIPLIS_SETTING_FINISH);
    	createIntentFilter(LiplisDefine.LIPLIS_SETTING_START);
    	createIntentFilter(LiplisDefine.LIPLIS_CLICK_ACTION_SLEEP);
    	createIntentFilter(LiplisDefine.LIPLIS_CLICK_ACTION_BATTERY);
    	createIntentFilter(LiplisDefine.LIPLIS_CLICK_ACTION_CLOCK);
    	createIntentFilter(LiplisDefine.LIPLIS_SETTING_START);
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : initEventHandler
    /// イベントハンドラの初期化
    /// </summary>
    protected void initEventHandler()
    {
    	setIvLiplisBodyListener();
    	setIvLiplisWindow();
    	setLlLpsLog();
    	setLlLpsSetting();
    	setLlLpsSleep();
    	setLlLpsAngClock();
    	setLlLpsbattery();
    }


    /// <summary>
    //  MethodType : child
    /// MethodName : createIntentFilter
    /// インテントフィルタークリエイター
    /// </summary>
	public void createIntentFilter (String Code)
	{
		try
		{
			IntentFilter filterBattery = new IntentFilter();
			filterBattery.addAction(Code);
			this.registerReceiver(mReceiver, filterBattery);
		}
		catch(Exception e)
		{
		}
	}

	/// <summary>
    //  MethodType : child
    /// MethodName : getFlgSize
    /// flgSizeの取得
    /// </summary>
	protected int getFlgSize()
	{
		try
		{
			return  this.getSharedPreferences(LiplisDefine.PREFS_NAME, 1).getInt(LiplisDefine.PREFS_NOW_SIZE, 1);
		}
		catch(Exception e)
		{
			return 1;
		}
	}



    /// <summary>
    //  MethodType : child
    /// MethodName : reSetUpdateCount
    /// チャットの開始
    /// </summary>
	protected void reSetUpdateCount()
	{
        try
    	{
    		cntUpdate = liplisInterval * 10;		//LiplisSmallの場合、レートを1/10で動作させているので、インターバルを10倍する
        	flgAlarm = 10;
    	}
    	catch(Exception e)
    	{
    	}
	}

    /// <summary>
    //  MethodType : child
    /// MethodName : createTimer
    /// チャットの開始
    /// </summary>
	protected void chatStart()
	{
        try
    	{
    		//フラグ
    		flgChatting = true;

    		//即表示判定
    		if(op.getLpsSpeed() == 3)
    		{
    			immediatelyReflesh();
    		}else
    		{
    			/// 2013/05/01 ver3.4.0 おしゃべり対応
    			if(op.getLpsVoice() == 1)
    			{
    				try
    				{
    					speechText();
    				}
    				catch(Exception ex)
    				{
    					Log.d("Liplis chatStart",ex.toString());
    				}
    			}
    			//おしゃべりフェーズに移行
    			flgAlarm = 12;
    		}
    	}
    	catch(Exception e)
    	{
    	}
	}

	/// <summary>
    //  MethodType : child
    /// MethodName : chatStop
    /// チャットストップ
    /// </summary>
	protected void chatStop()
	{
        try
    	{
        	//スルーカウントに移行
        	flgAlarm = 0;

        	//カウントの初期化
        	reSetUpdateCount();
    	}
    	catch(Exception e)
    	{
    	}
    	finally
    	{
    		flgChatting = false;
    		flgSkip = false;
    	}
	}

    ///====================================================================
    ///
    ///                            音声制御関連
    ///
    ///====================================================================


    /// <summary>
    //  MethodType : base
    /// MethodName : onInit
    /// tts用オーバーライドメソッド
    /// 2013/05/01 ver3.4.0 tts追加対応
    /// </summary>
	public void onInit(int status) {
        if (TextToSpeech.SUCCESS == status) {
        	//#TEST
        	//Locale locale = Locale.JAPANESE;
            Locale locale = Locale.ENGLISH;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                Log.d("Liplis onInit", "Error SetLocale");
            }
        } else {
            Log.d("Liplis onInit", "Error Init");
        }
	}

    /// <summary>
    /// MethodType : child
    /// MethodName : initTts
	/// 2013/05/01 ver3.4.0 おしゃべり対応
    /// ttsを初期化する
    /// </summary>
	public void initTts()
	{
		try
		{
	    	//音声オブジェクトの初期化
	        //ここで渡すコンテキストは、「getApplicationContext」で取得しないとエラーとなる。
	    	tts = new TextToSpeech(this.getApplicationContext(), this);

	    	//ピッチ設定
	    	onTtsSettingChange();
		}
		catch(Exception e)
		{
		}
	}

	/// <summary>
    //  MethodType : child
    /// MethodName : speechText
    /// 現在の話題を音声でおしゃべりする
	/// 2013/05/01 ver3.4.0 おしゃべり対応
    /// </summary>
	protected void speechText()
	{
		//ヌルチェック
		if(tts == null){initTts();}

		//現在の読み込みテキストを取得する
		String speechStr = getShortNewsText();

		//スピーチストリングの長さをチェック
		if (0 < speechStr.length()) {
			//読み上げ中チェック
			if (tts.isSpeaking()) {
				// 読み上げ中なら止める
				tts.stop();
			}

			// 読み上げ開始
			tts.speak(speechStr, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	/// <summary>
    //  MethodType : child
    /// MethodName : shudDonwnTts
    /// ttsをシャットダウンする
	/// 2013/05/01 ver3.4.0 おしゃべり対応
    /// </summary>
	public void shudDonwnTts()
	{
        if (null != tts) {
            // TextToSpeechのリソースを解放する

			if (tts.isSpeaking()) {
				// 読み上げ中なら止める
				tts.stop();
			}

            tts.shutdown();
        }
	}

    /// <summary>
    //  MethodType : base
    /// MethodName : onTtsSettingChange
    /// ttsの音声設定変更時
    /// 2013/05/01 ver3.4.0 おしゃべり対応
    /// </summary
    protected void onTtsSettingChange()
    {
		this.rate = op.getLpsVoiceRate() / 50.0f;
		if(this.rate < 0.1f){this.rate = 0.1f;}

		if(tts.setSpeechRate(this.rate) == TextToSpeech.ERROR) {
			Log.e("Liplis onTtsSettingChange","tts rate set Error : " + this.rate);
		}

		this.pitch = op.getLpsVoicePitch() / 50.0f;
		if(this.pitch < 0.1f){this.pitch = 0.1f;}

		if(tts.setPitch(this.pitch) == TextToSpeech.ERROR) {
			Log.e("Liplis onTtsSettingChange","tts pitch set Error" + this.pitch);
		}

    }

    ///====================================================================
    ///
    ///                            設定取得
    ///
    ///====================================================================



    /// <summary>
    //  MethodType : child
    /// MethodName : loadSetting
    /// 設定のロード(初期化の強制ロード)
    /// </summary>
    protected void loadSetting()
    {
    	try
    	{
    		op.getPreferenceData(this);
    		liplisInterval = op.getLpsInterval();
    		liplisRefresh	= op.getLpsReftesh();
        	chengeWindow();
        	onTtsSettingChange();


        	//時計モードへの対応
        	switchTalkClock(op.getLpsMode() == 4);
    	}
    	catch(Exception e)
    	{
    	}

    	//このメソッドの後にcreateTimerしているので、ここでは何もしない
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : chengeWindowCheck
    /// ウインドウが変更されていたら更新する
    /// </summary>
    protected void chengeWindowCheck()
    {
        try
    	{
        	//ウインドウコードが現在値と同じであれば抜ける
        	if(liplisWindowCode == op.getLpsWindowColor()){return;}
        	chengeWindow();
    	}
    	catch(Exception e)
    	{
    	}

    }


    /// <summary>
    //  MethodType : child
    /// MethodName : ウインドウを変更する
    /// </summary>
    protected void chengeWindow()
    {
        try
    	{
        	//ウインドウコード更新
        	liplisWindowCode = op.getLpsWindowColor();

            ivLiplisWindow.setImageResource( LiplisUtil.getWindowCode(liplisWindowCode));
    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : changeModeCheck
    /// モードが変更されているかチェックする
    /// </summary>
    protected void changeModeCheck()
    {
        try
    	{
        	//ウインドウコードが現在値と同じであれば抜ける
        	if(liplisInterval == op.getLpsInterval()){return;}

        	//時計モードへの対応
        	if(op.getLpsMode() == 4)
        	{
        		//強制起床
        		standUp();

        		//クロックモードオン
        		switchTalkClockCheck(true);
        	}
        	else
        	{
        		switchTalkClockCheck(false);
        	}

        	//インターバルの変更を取得
        	liplisInterval = op.getLpsInterval();

        	//インターバルが変更されているのでタイマーの生成しなおし
        	//createTimer(context);
    	}
    	catch(Exception e)
    	{
    	}
    }



    /// <summary>
    //  MethodType : child
    /// MethodName : updateBodySitDown
    /// 座り更新
    /// </summary>
    protected boolean updateBodySitDown()
    {
        try
    	{

            ivLiplisBody.setImageResource(R.drawable.sleep);
            txtLiplisTalkText.setText(LiplisDefine.SAY_ZZZ);

    		return true;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}


    }

    ///====================================================================
    ///
    ///                            データ取得
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : child
    /// MethodName : getShortNews
    /// ニュースの取得
    /// </summary>
    protected void getShortNews(){
        try
    	{
        	//2013/03/01 ver3.1.0 ニュースソースをLiplisNewsで管理する方式に変更。ランゲージコードも0を固定とした
        	//liplisNowTopic = LiplisApi.getShortNews(nameValuePair, op.getLpsLanguage());
        	liplisNowTopic = lpsNews.getShortNews(this, getNameValuePairForLiplisNews(), 0);

        	//バッファに退避
        	liplisPrvTopic = liplisNowTopic;

        	if(liplisNowTopic == null || liplisNowTopic.nameList.size() == 0)
        	{
        		liplisNowTopic = FctLiplisMsg.createMsgMassageDlFaild();
        	}
    	}
    	catch(Exception e)
    	{
    		Log.d("Liplis getShortNews",e.toString());
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : getNameValuePair
    /// 2013/03/01 ver3.1.0 POSTメッセージを非同期メソッドからも取得できるようにする
    /// POST用のメッセージを作成する
    /// </summary>
    protected List<NameValuePair> getNameValuePairForLiplisNews()
    {
    	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("tone", LiplisDefine.API_SHORT_NEWS_TONE));
    	nameValuePair.add(new BasicNameValuePair("newsFlg", op.getNewsFlg()));
    	//nameValuePair.add(new BasicNameValuePair("lnCode", LiplisUtil.convValIntToStr(op.getLpsLanguage())));

    	return nameValuePair;
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : getNameValuePairForLiplisNewsList
    /// 2013/03/01 ver3.3.0 一括取得対応のため新設
    /// 2013/04/29 ver3.3.7 ニュースレンジを設定するように追加。ユーザーIDを追加
    /// POST用のメッセージを作成する
    /// </summary>
    protected List<NameValuePair> getNameValuePairForLiplisNewsList()
    {
    	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
    	nameValuePair.add(new BasicNameValuePair("userid", op.getLpsUid()));
    	nameValuePair.add(new BasicNameValuePair("tone", LiplisDefine.API_SHORT_NEWS_TONE));
    	nameValuePair.add(new BasicNameValuePair("newsFlg", op.getNewsFlg()));
    	nameValuePair.add(new BasicNameValuePair("num", String.valueOf(LiplisDefine.LPS_NEWS_QUEUE_GET_CNT)));
    	nameValuePair.add(new BasicNameValuePair("hour", op.getLpsNewsRangeStr()));
    	nameValuePair.add(new BasicNameValuePair("already", String.valueOf(op.getNewsAlready())));
    	nameValuePair.add(new BasicNameValuePair("twitterMode", String.valueOf(op.getTopicTwitterMode())));
    	nameValuePair.add(new BasicNameValuePair("runout", String.valueOf(op.getRunOut())));

    	return nameValuePair;
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : getShortNewsText
    /// ニュースのテキストを取得する
    /// 2013/05/01 ver3.4.0 おしゃべり対応
    /// </summary>
    protected String getShortNewsText()
    {
    	if(liplisNowTopic != null && liplisNowTopic.nameList.size() != 0)
    	{
    		//ストリングバッファ
    		StringBuffer result = new StringBuffer(100);

    		//ネームを回して文章を復元する
    		for(String str : liplisNowTopic.nameList)
    		{
    			result.append(str);
    		}

    		//文字列を返す
    		return result.toString();
    	}
    	else
    	{
    		return "";
    	}
    }

    ///====================================================================
    ///
    ///                           アイコンの更新
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : child
    /// MethodName : updateBatteryInfo
    /// バッテリー情報の更新
    /// </summary>
    protected boolean updateBatteryInfo(Intent intent)
    {
    	try
    	{
    		if(flgIconOn)
    		{
    			//バッテリーイメージの取得
				obt.getBatteryImage(intent);

				//バッテリーIDの取得
				batteryNowId =  obt.getBatteryImageId();

				//バッテリーアイコンの
				ivLiplisBattery.setImageResource(batteryNowId);
    		}

    		return true;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName :  updateSleepIcon
    /// スリープアイコンの更新
    /// </summary>
    protected boolean updateSleepIcon()
    {
    	try
    	{
            if(flgSitdown)
            {
            	ivLiplisSleep.setImageResource(R.drawable.ico_waikup);
            }
            else
            {
            	ivLiplisSleep.setImageResource(R.drawable.ico_zzz);
            }

            return true;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : updateSleepIcon
    /// 考え中アイコンの更新
    /// </summary>
    protected boolean updateThinkingIcon()
    {
    	try
    	{
            if(flgThinking)
            {
            	ivLiplisThinking.setImageResource(R.drawable.ico_thinking);
            }
            else
            {
            	ivLiplisThinking.setImageResource(R.drawable.ico_thinking_not);
            }

            return true;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }

    ///====================================================================
    ///
    ///                          チャット処理
    ///
    ///====================================================================

    /// <summary>
    //  MethodType : child
    /// MethodName : updateLiplis
    /// リプリスの更新
    /// 次の表示
    /// </summary>
    protected void updateLiplis()
    {
    	try
    	{
    		refleshLiplis();
    	}
    	catch(Exception e)
    	{
    	}


    }



    /// <summary>
    //  MethodType : child
    /// MethodName : nextLiplis
    /// 次の表示
    /// </summary>
    protected void nextLiplis( )
    {
        try
    	{
	    	//話題取得フェーズ終了まで0に設定
	    	flgAlarm = 0;

	    	//2013/10/05 ver3.5.0 話題が尽きた時の動作
	    	if(runoutCheck())
	    	{
	    		reSetUpdateCount();
	    		return;
	    	}

	    	//おしゃべり/ふつう/ひかえめ
	    	if(op.getLpsMode() == 0 || op.getLpsMode() == 1 || op.getLpsMode() == 2)
	    	{
	    		runLiplis();
	    	}
	    	//無口
	    	else if(op.getLpsMode() == 3)
	    	{
	    		//何もしない
	    	}
	    	//時計＆バッテリー
	    	else if(op.getLpsMode() == 4)
	    	{
	    		runClock();
	    	}
    	}
    	catch(Exception e)
    	{
    		flgAlarm = 11;
    	}
    }
    protected void nextClick()
    {
        try
    	{
        	//時計
        	if(op.getLpsMode() == 4)
        	{
        		runClock();
        	}
        	//発言
        	else
        	{
        		runLiplis();
        	}

    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    /// MethodType : child
    /// MethodName : runoutCheck
    /// 2013/10/05 ver3.5.0 話題が尽きた時の動作
    /// 話題の残量チェック
    /// </summary>
    protected boolean runoutCheck()
    {
    	//2013/10/05 ver3.5.0 話題が尽きた時の動作
    	boolean result = op.getRunOut() == 1 && !lpsNews.checkNewsQueueCount(this,getNameValuePairForLiplisNewsList());
    	if(result)
    	{
    		reSetUpdateCount();
    	}
    	return result;
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : runLiplis
    /// 行動する
    /// </summary>
    protected void runLiplis()
    {
        try
    	{
	    	//チャット中なら回避
	    	if(flgChatting){chatStop(); return;}

	    	//座り中なら回避
	    	if(flgSitdown){chatStop(); return;}

	    	//ウインドウチェック ver3.0 2012/03/31
	    	windowOn();

        	//クロックチェック
        	switchTalkClockCheck(false);

        	//アイコンカウント
        	iconCloseCheck();

        	//たち絵をデフォルトに戻す？
        	//returnDefaultBody();
        	returnDefaultBody();

        	//トピックを取得する
        	getTopic();

        	//チャット情報の初期化
        	initChatInfo();

        	//ニュースを取得しておしゃべり
        	chatStart();
    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : initChatInfo
    /// チャット情報の初期化
    /// 行動する
    /// </summary>
    protected void initChatInfo()
    {
        try
    	{
        	//チャットテキストの初期化
        	liplisChatText = "";

        	//ナウワードの初期化
        	liplisNowWord = "";

        	//ナウ文字インデックスの初期化
        	cntLct = 0;

        	//ナウワードインデックスの初期化
        	cntLnw = 0;
    	}
    	catch(Exception e)
    	{
    	}

    }

    /// <summary>
    //  MethodType : child
    /// MethodName : iconCloseCheck
    /// アイコン閉じのチェック
    /// </summary>
    protected void iconCloseCheck()
    {
        try
    	{
        	//アイコン消去が有効な場合
        	if(op.getLpsIconCloseCnt() >= 0)
        	{
        		if(cntIconClose > 0)
            	{
            		cntIconClose--;
            	}
        		else if(cntIconClose == 0)
        		{
        			iconOff();
        			cntIconClose = -1;
        		}
        	}
        	else
        	{
        		//アイコンを常に表示設定で、フラグがオフのときは表示
        		if(!flgIconOn)
        		{
        			iconOn();
        		}
        	}
    	}
    	catch(Exception e)
    	{
    	}
    }



    /// <summary>
    //  MethodType : child
    /// MethodName : getTopic
    /// トピックを取得する
    /// </summary>
    protected void getTopic()
    {
        try
    	{
        	flgThinking = true;
        	updateThinkingIcon();

        	//時報チェック

        	//バッテリー変化チェック

            getShortNews();

        	//flgGettingTopic = false;

        	flgThinking = false;
        	updateThinkingIcon();
    	}
    	catch(Exception e)
    	{
    		flgThinking = false;
    		updateThinkingIcon();
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : switchTalkClockCheck
    /// トークウインドウとクロックウインドウをスイッチする必要があるか調べ、必要であればスイッチ
    ///	これから変更するべきフラグを指定する
    /// </summary>
    protected void switchTalkClockCheck(boolean targetBool)
    {
    	if(targetBool != flgClockMode)
    	{
    		switchTalkClock(targetBool);
    	}
    }
    protected void switchTalkClock(boolean target)
    {
        flgClockMode = target;

        if(target)
        {
        	//時計を更新しておく
        	oc.updateClockObject();

        	llTalkText.setVisibility(View.GONE);
        	llClockAndBattery.setVisibility(View.VISIBLE);

        	updateClock();
        }
        else
        {
        	llTalkText.setVisibility(View.VISIBLE);
        	llClockAndBattery.setVisibility(View.GONE);
        }
    }


    /// <summary>
    //  MethodType : child
    /// MethodName : refleshLiplis
    /// リフレッシュメソッド
    /// </summary>
    protected void refleshLiplis()
    {
        try
    	{
            //--- キャンセルフェーズ --------------------
            if(checkMsg()){return;}

            //すわりチェック
            if(checkSitdown()){return;}

            //スキップチェック
            if(checkSkip())
            {
    			updateText();
            }

            //--- ナウワード取得・ナウテキスト設定フェーズ --------------------
            if(setText()){return;}

            //--- 描画フェーズ --------------------
        	updateText();

        	//ボディの更新
    		updateBody();

    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : immediatelyChat
    /// 即座の行動
    /// </summary>
    protected void immediatelyReflesh()
    {
        try
    	{
            //--- キャンセルフェーズ --------------------
            if(checkMsg()){return;}

            //すわりチェック
            if(checkSitdown()){return;}

            //--- 即表示フェーズ --------------------
            //スキップ
            skipLiplis();

            //--- 描画フェーズ --------------------
        	updateText();

        	//ボディの更新
    		updateBody();

            //終了
            checkEnd();
    	}
    	catch(Exception e)
    	{
    	}
    }

    /// <summary>
    //  MethodType : child
    /// MethodName : checkMsg
    /// ver3.4.2 再配置時にろーでぃんぐなうで止まる問題への対応
    /// </summary>
    protected void refleshOnly()
    {

    }


    /// <summary>
    //  MethodType : child
    /// MethodName : checkMsg
    /// メッセージチェック
    ///	１バッチ終わったときにメッセージをヌルにしている。
    ///	次が読み込まれるまではヌルでアイドル状態なので、抜ける。
    /// </summary>
    protected boolean checkMsg()
    {
        try
    	{
        	 if(liplisNowTopic == null)
             {
             	reSetUpdateCount();
             	return true;
             }
             return false;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}

    }

    /// <summary>
    //  MethodType : child
    /// MethodName : checkSkip
    /// スキップチェック
    /// </summary>
    protected boolean checkSkip()
    {
        try
    	{
        	if(flgSkip)
            {
            	return skipLiplis();
            }
            return false;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}

    }

    /// <summary>
    //  MethodType : child
    /// MethodName : checkSkip
    /// スキップチェック
    /// </summary>
    protected boolean skipLiplis()
    {
        try
    	{
        	liplisChatText = "";

        	for(int idx = 0; idx < liplisNowTopic.nameList.size(); idx++)
        	{
                //--- ワードセット、感情チェックフェーズ --------------------
                //送りワード文字数チェック
        	    if (idx != 0) {
                    //なうワードの初期化
        	    	liplisNowWord = liplisNowTopic.nameList.get(idx);

                    //プレブエモーションセット
                    prvEmotion = nowEmotion;

                    //なうエモーションの取得
                    nowEmotion = liplisNowTopic.emotionList.get(idx);

                    //なうポイントの取得
                    nowPoint = liplisNowTopic.pointList.get(idx);
        	    }
        	    //初回ワードチェック
        	    else if (idx == 0) {

        	    	liplisNowWord = liplisNowTopic.nameList.get(idx);

        	        //空だったら、空じゃなくなるまで繰り返す
        	        if (liplisNowWord.equals("")) {
        	            do {
        	                //次ワード遷移
        	                idx++;

        	                //終了チェック
        	                if (idx > liplisNowTopic.nameList.get(idx).length()) {break;}

        	                //ナウワードの初期化
        	                liplisNowWord = liplisNowTopic.nameList.get(idx);

        	            } while (liplisNowWord.equals(""));
        	        }
        	    }
        	    //おしゃべり中は何もしない
        	    else {

        	    }

        	    for(int kdx = 0; kdx < liplisNowWord.length(); kdx++)
        	    {
                    //おしゃべり
                    liplisChatText = liplisChatText + liplisNowWord.substring(kdx, kdx + 1);
        	    }

        	    //終端設定
        	    cntLnw = liplisNowTopic.nameList.size();
        	    cntLct = liplisNowWord.length();

        	}
        	return true;
    	}
    	catch(Exception e)
    	{
    		//終端設定
    	    cntLnw = liplisNowTopic.nameList.size();
    	    cntLct = liplisNowWord.length();

    		return false;
    	}


    }

    /// <summary>
    //  MethodType : child
    /// MethodName : checkSitdown
    /// すわりチェック
    /// </summary>
    protected boolean checkSitdown()
    {
        try
    	{
        	if(flgSitdown)
        	{
        		liplisNowTopic = null;
        		updateBodySitDown();
        		return true;
        	}
        	return false;
    	}
    	catch(Exception e)
    	{
    		 return false;
    	}

    }

    /// <summary>
    //  MethodType : child
    /// MethodName : checkEnd
    /// 終了チェック
    /// </summary>
    protected boolean checkEnd()
    {
        try
    	{
        	if(cntLnw >= liplisNowTopic.nameList.size())
        	{
        		ol.append(liplisChatText,liplisNowTopic.url);
        		chatStop();
        		herfEyeCheck();
        		liplisNowTopic = null;
        		return true;
        	}
        	return false;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }


    /// <summary>
    //  MethodType : child
    /// MethodName : getSetText
    /// ナウワード設定とテキスト設定を行う
    /// </summary>
    protected boolean setText()
    {
        try
    	{
        	//配列チェック



            //送りワード文字数チェック
    	    if (cntLnw != 0) {
    	    	if(cntLct >= liplisNowWord.length())
    	    	{
    	    		//終了チェック
    	    		if(checkEnd()){return true;}

    		    	//チャットテキストカウントの初期化
    		    	cntLct = 0;

    	            //なうワードの初期化
    		    	liplisNowWord = liplisNowTopic.nameList.get(cntLnw);

    	            //プレブエモーションセット
    	            prvEmotion = nowEmotion;

    	            //なうエモーションの取得
    	            nowEmotion = liplisNowTopic.emotionList.get(cntLnw);

    	            //なうポイントの取得
    	            nowPoint = liplisNowTopic.pointList.get(cntLnw);

    		    	//インデックスインクリメント
    		    	cntLnw++;
    	    	}
    	    }
    	    //初回ワードチェック
    	    else if (cntLnw == 0) {

    	    	//チャットテキストカウントの初期化
    	    	cntLct = 0;

    	    	//なうワードの初期化
    	    	liplisNowWord = liplisNowTopic.nameList.get(cntLnw);

    	    	//次ワード遷移
            	cntLnw++;

    	        //空だったら、空じゃなくなるまで繰り返す
    	        if (liplisNowWord.equals("")) {
    	            do {
    	            	//チェックエンド
    	            	checkEnd();

    	                //終了チェック
    	                if (cntLnw > liplisNowTopic.nameList.get(cntLnw).length()) {break;}

    	                //ナウワードの初期化
    	                liplisNowWord = liplisNowTopic.nameList.get(cntLnw);

    	                //次ワード遷移
    	            	cntLnw++;

    	            } while (liplisNowWord.equals(""));
    	        }
    	    }
    	    //おしゃべり中は何もしない
    	    else {

    	    }

    	    //おしゃべり
    	    liplisChatText = liplisChatText + liplisNowWord.substring(cntLct, cntLct+1);
    	    cntLct++;

    	    return false;
    	}
    	catch(Exception e)
    	{
    		//終端設定
    	    cntLnw = liplisNowTopic.nameList.size();
    	    cntLct = liplisNowWord.length();

    	    //チャットを中断する
    	    checkEnd();

    		return false;
    	}

    }



    //  MethodType : child
    /// MethodName : updateBody
    /// おてんば(フル動作)
    /// </summary>
    protected boolean updateBody()
    {
    	try
    	{
    		//感情変化(OBをセットする)
    		setObjectBody();

	        //--- 口パク --------------------
	        //口パクカウント
    		if(flgChatting)
    		{
    	        if(cntMouth == 1)	{cntMouth=2;}
    	        else				{cntMouth=1;}
    		}
    		else
    		{
    			cntMouth=1;
    		}

	        //--- 目パチ --------------------
	        //目パチカウント
	        if(cntBlink == 0){cntBlink = getBlinkCnt();}
	        else{cntBlink--;}

	        //--- 描画 --------------------
	        //画像はすべてできているので、感情変化を意識する必要はない
	        ivLiplisBody.setImageResource(ob.getLiplisBodyId(getBlinkState(), cntMouth));

	        return true;
		}
		catch(Exception e)
		{
			olb = new ObjLiplisBody();
			return false;
		}
    }

  /// <summary>
    //  MethodType : child
    /// MethodName : setObjectBody
    /// 2013/10/27 ver3.6.0 バッテリー状態を反映する
    /// </summary>
    protected boolean setObjectBody()
    {
    	try
    	{
    		//感情変化
    		if(nowEmotion != prvEmotion && flgChatting){
        		if(op.getLpsHealth() == 1 && obt.getBatteryNowLevel() < 75 )
        		{
        			//ヘルス設定ONでバッテリー残量75%以下なら、小破以下の画像取得
        			ob = olb.getLiplisBodyHelth(obt.getBatteryNowLevel(),nowEmotion,nowPoint);
        		}
        		else
        		{
        			ob = olb.getLiplisBody(nowEmotion,nowPoint);
        		}
    		}

    		return true;
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    }

    //  MethodType : child
    /// MethodName : updateBodyOtenba
    /// おてんば(フル動作)
    /// </summary>
    protected boolean updateText()
    {
    	try
    	{
	        txtLiplisTalkText.setText(liplisChatText);

	        return true;
		}
		catch(Exception e)
		{
			return false;
		}
    }

    //  MethodType : child
    /// MethodName : runClock
    /// 時計、バッテリー表示
    /// </summary>
    protected void runClock()
    {
        try
    	{
        	//ウインドウチェック
        	if(!flgWindowOn){windowOn();};

        	//クロックチェック
        	switchTalkClockCheck(true);

        	//時計バッテリー表示の更新
        	refleshClockAndBattery();

        	//アイコンカウント
        	iconCloseCheck();

        	//フラグ設定
        	reSetUpdateCount();
    	}
    	catch(Exception e)
    	{
        	Log.d("Liplis runClock",e.getMessage());
    	}
    }

    //  MethodType : child
    /// MethodName : runClockTolk
    /// 時計、バッテリーの発言
    /// </summary>
    protected void runClockTolk()
    {
        try
    	{

    	}
    	catch(Exception e)
    	{
    	}
    }

    //  MethodType : child
    /// MethodName : updateClockAndBattery
    /// 時計、バッテリー表示の更新
    /// </summary>
    protected void refleshClockAndBattery()
    {
        try
    	{
            //時刻オブジェクトの更新
            oc.updateClockObject();
            //時計＆バッテリー表示の更新
            updateClock();
        	//ボディの更新
            cntBlink = 10;		//まばたきさせない
    		updateBody();
    	}
    	catch(Exception e)
    	{

    	}
    }

    //  MethodType : child
    /// MethodName : updateClock
    /// 時計＆バッテリーの表示更新
    /// </summary>
    protected boolean updateClock()
    {
    	int batteryRate = 0;
    	try
    	{
    		ivImYear1.setImageResource(oc.getYear1());
    		ivImYear1.setImageResource(oc.getYear2());
    		ivImYear1.setImageResource(oc.getYear3());
    		ivImYear1.setImageResource(oc.getYear4());

    		ivImMonth1.setImageResource(oc.getMonth1());
    		ivImMonth2.setImageResource(oc.getMonth2());
    		ivImDay1.setImageResource(oc.getDay1());
    		ivImDay2.setImageResource(oc.getDay2());

    		ivImHour1.setImageResource(oc.getHour1());
    		ivImHour2.setImageResource(oc.getHour2());
    		ivImMin1.setImageResource(oc.getMin1());
    		ivImMin2.setImageResource(oc.getMin2());

    		batteryRate = obt.getBatteryNowLevel();


    		if(batteryRate < 5)
    		{
    			ivImBattery01.setImageResource(R.drawable.battery_non);
    			ivImBattery02.setImageResource(R.drawable.battery_non);
    			ivImBattery03.setImageResource(R.drawable.battery_non);
    			ivImBattery04.setImageResource(R.drawable.battery_non);
    			ivImBattery05.setImageResource(R.drawable.battery_non);
    			ivImBattery06.setImageResource(R.drawable.battery_non);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 10)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.battery_non);
    			ivImBattery03.setImageResource(R.drawable.battery_non);
    			ivImBattery04.setImageResource(R.drawable.battery_non);
    			ivImBattery05.setImageResource(R.drawable.battery_non);
    			ivImBattery06.setImageResource(R.drawable.battery_non);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 20)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.battery_non);
    			ivImBattery04.setImageResource(R.drawable.battery_non);
    			ivImBattery05.setImageResource(R.drawable.battery_non);
    			ivImBattery06.setImageResource(R.drawable.battery_non);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 30)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.battery_non);
    			ivImBattery05.setImageResource(R.drawable.battery_non);
    			ivImBattery06.setImageResource(R.drawable.battery_non);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 40)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.battery_non);
    			ivImBattery06.setImageResource(R.drawable.battery_non);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 50)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery06.setImageResource(R.drawable.battery_non);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 60)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery06.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery07.setImageResource(R.drawable.battery_non);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 70)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery06.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery07.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery08.setImageResource(R.drawable.battery_non);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 80)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery06.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery07.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery08.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery09.setImageResource(R.drawable.battery_non);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate < 90)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery06.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery07.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery08.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery09.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery10.setImageResource(R.drawable.battery_non);
    		}
    		else if(batteryRate > 90)
    		{
    			ivImBattery01.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery02.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery03.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery04.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery05.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery06.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery07.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery08.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery09.setImageResource(R.drawable.ico_batterygage);
    			ivImBattery10.setImageResource(R.drawable.ico_batterygage);
    		}

    		//--- 描画 --------------------
	        //感情値とポイントをランダムで取得
    		Random rnd = new Random();

            int ran = rnd.nextInt(9) + 1;
            int point = rnd.nextInt(3) - 1 ;

    		ob = olb.getLiplisBody(ran,point);

    		//描画
	        ivLiplisBody.setImageResource(ob.getLiplisBodyId(1, 0));

	        return true;
		}
		catch(Exception e)
		{
			return false;
		}
    }


    ///====================================================================
    ///
    ///                           定型おしゃべり
    ///
    ///====================================================================

    //  MethodType : child
    /// MethodName : greet
    /// 挨拶する
    /// </summary>
    protected void greet()
    {
        try
    	{
        	//時計モードの場合は発言させない
        	//if(op.getLpsMode() == 4){chatStart(context);return;}

        	//挨拶の選定
        	liplisNowTopic = olc.getGreet();

        	//空だったらろーでぃんぐなう♪
        	if(liplisNowTopic.getMessage().equals(""))
        	{
        		liplisNowTopic = new MsgShortNews("Lading Now",0,0);
        	}

        	//チャット情報の初期化
        	initChatInfo();

        	//おしゃべりスレッドスタート
        	chatStart();
    	}
    	catch(Exception e)
    	{
    	}

    }

    //  MethodType : child
    /// MethodName : batteryInfo
    /// バッテリー情報のお知らせ
    /// </summary>
    protected void batteryInfo()
    {
        try
    	{
        	//座り中なら回避
        	if(flgSitdown){return;}

        	//挨拶の選定
        	liplisNowTopic = olc.getBatteryInfo(obt.getBatteryNowLevel());

        	//空だったらろーでぃんぐなう♪
        	if(liplisNowTopic.getMessage().equals(""))
        	{
        		liplisNowTopic = new MsgShortNews(obt.getBatteryNowLevel() + "%",0,0);
        	}

        	//チャット情報の初期化
        	initChatInfo();

        	//おしゃべりスレッドスタート
        	chatStart();
    	}
    	catch(Exception e)
    	{
    	}
    }

    //  MethodType : child
    /// MethodName : batteryInfo
    /// 時刻情報のお知らせ
    /// </summary>
    protected void clockInfo()
    {
        try
    	{
        	//座り中なら回避
        	if(flgSitdown){return;}

        	//挨拶の選定
        	liplisNowTopic = olc.getClockInfo();

        	//空だったら現時時刻のみを返す
        	if(liplisNowTopic.getMessage().equals(""))
        	{
        		liplisNowTopic = new MsgShortNews(LiplisUtil.getNowTime(Calendar.MINUTE),0,0);
        	}

        	//チャット情報の初期化
        	initChatInfo();

        	//おしゃべりスレッドスタート
        	chatStart();
    	}
    	catch(Exception e)
    	{
    	}
    }



    ///====================================================================
    ///
    ///                       ボディ更新詳細処理
    ///
    ///====================================================================

    //  MethodType : initStand
    /// MethodName : getBlinkCnt
    /// まばたきカウントの取得
    /// </summary>
    protected void returnDefaultBody()
    {
        try
    	{
            //--- キャンセルフェーズ --------------------
            if(checkMsg()){return;}

            //すわりチェック
            if(checkSitdown()){return;}

            //からテキストをセット
            txtLiplisTalkText.setText("");

            ob = olb.getLiplisBody(0,0);

    		//描画
	        ivLiplisBody.setImageResource(ob.getLiplisBodyId(1, 0));

        }
    	catch(Exception e)
    	{
    	}
    }

    //  MethodType : child
    /// MethodName : getBlinkCnt
    /// まばたきカウントの取得
    /// </summary>
    protected int getBlinkCnt()
    {
    	try
    	{
			Random rnd = new Random();
			return rnd.nextInt(17) + 17;
    	}
    	catch(Exception e)
    	{
    		return 10;
    	}
    }

    /// <summary>
    /// getBlinkCnt
    /// まばたきカウントの取得
    /// </summary>
    protected int getBlinkState()
    {
        try
    	{
        	switch(cntBlink)
        	{
        	case 0:
        		return 1;
        	case 1:
        		return 2;
        	case 2:
        		return 3;
        	case 3:
        		return 2;
        	default:
        		return 1;
        	}
    	}
    	catch(Exception e)
    	{
    		return 1;
    	}
    }

    /// <summary>
    /// getBlinkCnt
    /// まばたきカウントの取得
    /// </summary>
    protected void herfEyeCheck()
    {
    	if(cntBlink == 1 || cntBlink == 3)
    	{
    		cntBlink = 0;
			updateBody();
    	}
    }



}

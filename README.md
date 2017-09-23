# 自定义控件

* Android自身带的控件不能满足需求, 需要根据自己的需求定义控件.

# 自定义控件可以分为三大类型

## 1. 组合已有的控件实现

### (1)优酷菜单（分两步）

* 静态界面先布置好
* 添加事件（实现动画逻辑）

- 逻辑
	- 三层布局：从外到里： level3, level2(ib_menu), level1(ib_home)
	- 点击 ib_menu: 
		- level3 立即隐藏或显示
	- 点击 ib_home:
		- level3 立即隐藏 且 level2 延迟隐藏 （level3 显示的情况下）
		- level2 立即隐藏 （level3 已隐藏的情况下）
		
		- level2 立即显示
		- 选择判断条件：先从大的分类开始（隐藏动画和显示动画），所以前两种情况为一组，第三种情况为一组。对第一组再次分组(level3隐藏与否)  [ **if的判断条件应该从大分类到小分类划分，情况越复杂越是要如此** ]

				case R.id.ib_home:
				    if (isLevel2Dispaly) { //隐藏动画
				        int delay = 0;
				        if (isLevel3Dispaly) {//level3显示时
				            AnimationUtil.rotateOutAnim(rl_level3, 0);
				            delay += 200;
				            isLevel3Dispaly = false;
				        }
				        AnimationUtil.rotateOutAnim(rl_level2, delay);
				        isLevel2Dispaly = false;
				    } else { //显示动画
				        AnimationUtil.rotateInAnim(rl_level2, 0);
				        isLevel2Dispaly = true;
				    }
				
				    break;
			
	- 点击 menu 按键
		- level3 立即隐藏 且 level2 延迟隐藏 且 level1 延迟隐藏 （level3 显示的情况下）
		- level2 立即隐藏 且 level1 延迟隐藏 （level3 已隐藏的情况下）
		- level1 立即隐藏 （level2 已隐藏的情况下）
		
		- level1 立即显示 且 level2 延迟显示 且 level3 延迟显示
		

- 注意

		1. 当有动画正在执行的时候，不响应点击事件 （ib_menu, ib_home, menu物理按键） 【AnimationUtil中设置一个共享变量runningAnimationCount，给动画设置监听器】
		2. 当布局隐藏的时候，点击原有区域不响应点击事件 （补间动画的不足，属性动画就没有这种现象。补间动画并没有真正改变View的位置）
		
		3. relativeLayout中两个View重叠放置，但是能够透过上一层控件(比如RelativeLayout)点击到下一层控件(Button)的现象：
		
			解决方法：在放在上面的那个控件设置clickable属性为true，或者设置setOnClickListener(null)，这样点击事件就不会透传到下一个控件上了. 原理就是 Android TouchEvent事件传递机制（横向分析），本质就是 设置 dispatchTouchEvent()放回true. 
		
		4. ImageButton 默认会有一个灰色的背景。去掉的方法：
		
		    android:background="@null" //不要背景
		    // 或者
		    android:background="@android:color/transparent" // 把背景设为透明
		
		5. 所有图片都是一个正方形的，其中是有透明部分的，而且美工也把某些图片的旋转效果都做好了

### (2)轮播图

实现： 使用 ViewPager 来实现

特点

1. 无限滑动 （getCount()返回Integer.MAX_VALUE, 初始化时 mViewPager.setCurrentItem(5000000); 设置一个较大的值） 
2. 自动轮播 （定时器中执行 mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1); ）

### (3) 下拉列表
- PopupWindow 的使用步骤(**3步 创建、设置 和显示**)
	- new PopupWindow() 对象（需要View，和with,height参数）
	- 设置可获取焦点 和 背景， 否则是/点击 外部 或 返回键，无法dismiss弹框 （可获取焦点也可以在PopupWindow的构造器中设置）

			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空的(透明)背景


	- show --> 有两种显示方法：showAsDropDown(), showAtLocation()
- ListView 设置 setOnItemClickListener(), 点击 ItemView 无响应的情况的解决方案
	- ItemView 根标签设置 android:descendantFocusability="blocksDescendants"属性。
	- 原因：就是ItemView布局中的 可点击的控件（Button, CheckBox等）抢夺了焦点


### 2. 完全自定义控件.(继承View, ViewGroup)
* 1. 自定义开关

思路：

	1. onDraw() 中通过 switchSate 值, 绘制开关状态
 	2. 通过 onTouchEvent 改变 switchSate 的值 以及绘制滑块移动效果
    3. ACTION_UP 时，适时调用回调接口方法


	- 自定义属性的步骤：
	- 1. attrs.xml 中声明 declare-styleable 节点
	- 2. 布局文件中使用，要声明命名空间（AS:res-auto结尾， Eclipse：包名结尾）
	- 3. 构造器中使用 AttributeSet 获取属性值
	
### 3. 继承已有的控件实现(扩展已有的功能)

1. 下拉刷新控件

	* listView.addHeaderView(headerView) 和 listView.addFooterView(footerView) 必须在 listView.setAdapter(mAdapter) 之前调用，否则会报错导致程序崩溃. 
	* listView 中的 HeaderView 和 footerView 是和 列表项（ItemView） 一起滑动的
	* 若设置了头布局，则 listView.getFirstVisiblePosition() 在初始的时候指的就是头布局的索引,从0开始。 同理若设置了尾布局，则getLastVisiblePosition() 指向的是尾布局的索引。 且 listView.getCount() = 数据项的个数 + 2（头布局和尾布局）。 BaseAdapter中也有一个getCount()方法，这两个getCount()方法是不同的
	* listView.setSelection(listView.getCount()-1);//滑到最底部。其实这个值是不会出现角标越界的情况，所以即使设置Integer.MAX_VALUE也没有问题
	* 设置 ProgressBar 的环形动画：android:indeterminateDrawable 
		* 创建一个shape， 设置形状为 环形（ring）, 设置 useLevel 为 false,  使用渐变效果(gradient)
		* 在shape 外边嵌套一个旋转动画标签 rotate
	* 提前手动测量View宽高

			//提前手动测量宽高,按照原有规则测量（即xml或者java中设置的宽高padding等相关属性）。否则getMeasuredHeight()获取为0,
			mHeaderView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);// 或 mesure(0,0);

	* 拉下刷新的原理：（主要就是 `ACTION_MOVE` 和 `ACTION_UP` 的处理）
		* 先给头布局设置paddingTop的值为负值，使得头布局刚好隐藏；
		* 接着就是根据手势下滑的距离(ACTION_MOVE)不断改变头布局的paddingTop的值，使得头布局慢慢显示出来
		* 根据头布局是否完全显示，更新状态和头布局中的相关View
		* 松手之后(ACTION_UP)根据头布局是否完全显示，决定是否执行刷新，和更改状态为刷新中...
		* ACTION_UP中回调下拉刷新的方法
		* 添加刷新完成方法：onRefreshComplete
		* 添加上拉加载更多：设置 OnScrollListener， 
			* if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && getLastVisiblePosition() == getCount() - 1) 开始加载更多
		

					   /**
				         * SCROLL_STATE_IDLE = 0; // 空闲
				         * SCROLL_STATE_TOUCH_SCROLL = 1; // 触摸滑动
				         * SCROLL_STATE_FLING = 2; // （由于惯性）滑翔
				         */




2. 侧滑控件
	* leftMenu 布局中的 ScrollView 根标签 和 其子标签 LinearLayout 都必须同时设置240dp，才有效。即 leftMenu.getMeasuredWidth() 值为 480px， 才是符合我们预期的。当 ScrollView 和 LinearLayout 只有一个设置为 240dp 时，leftMenu.getMeasuredWidth() 值为 827px, 比屏幕 768px 还宽。 不知道为什么。
	* 公式：int offsetX = (int) (lastX - moveX); // 注意相减的顺序不要弄反了. 向左滑：窗口（滚动条）向右移动（正数）; 向右滑：窗口(滚动条)向左移动(负数)。 窗口（滚动条）的移动刚好和滑动方向是相反的
	* getScrollX();获取当前x轴滚动的距离
	* Scroller的使用步骤

			1. 创建Scroller的实例  scroller = new Scroller(getContext());
			2. 调用startScroll()方法来开始模拟滚动数据并刷新界面 
				
				ACTION_UP时
				scroller.startScroll(startX, 0, dx, 0, duration);
				invalidate(); //会导致View.draw()被调用 ,而View.draw()内部又会调用 computeScroll();
	
			3. 在重写的computeScroll()方法中，不断获取模拟的数值，完成平滑滚动的逻辑 
			
			    @Override
			    public void computeScroll() {
			        super.computeScroll();
			
			        if (scroller.computeScrollOffset()) {// 若duration没有结束，则返回true
			
			            int currX = scroller.getCurrX();
			            KLog.d("currX: " + currX);
			            scrollTo(currX, 0);
			            invalidate();//scrollTo自带invalidate(), 所以这里可以不用写也可以。但是在之前比较老的版本中必须要加上
			        }
			    }
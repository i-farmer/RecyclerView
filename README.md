# RecyclerView

## RecyclerTabView指示器
#### 1.代码使用（同时支持ViewPager、ViewPager2，支持横竖向）
```
  RecyclerTabView.setAdapter(A extends RecyclerTabViewAdapter）
  RecyclerTabView.setUpWithViewPager
  RecyclerTabView.setUpWithViewPager2
  注：绑定之前，必须设置RecyclerTabView和ViewPager的适配器，且数量要相等
  
  RecyclerTabView.setCallback（可选）
```
#### 2.自定义指示器
```
  <i.farmer.widget.recyclerview.tabs.RecyclerTabView
    android:id="@+id/mTabView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    app:indicatorStyle="none"
    app:tabItemSpacing="@dimen/x40"
    app:tabPaddingEnd="@dimen/x60"
    app:tabPaddingStart="@dimen/x40" />
    
  然后在代码中增加自定义指示器
  RecyclerTabView.addItemDecoration(D extends RecyclerTabViewIndicator)  
```
#### 3.线条指示器（默认）
```
  <i.farmer.widget.recyclerview.tabs.RecyclerTabView
    android:id="@+id/mTabView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:paddingTop="@dimen/x20"
    android:paddingBottom="@dimen/x10"
    app:indicatorColor="#1A1A1A"
    app:indicatorGap="always"
    app:indicatorHeight="@dimen/x10"
    app:indicatorStyle="line"
    app:indicatorWidth="@dimen/x28"
    app:tabItemSpacing="@dimen/x40"
    app:tabPaddingEnd="@dimen/x60"
    app:tabPaddingStart="@dimen/x60" />
    其中：如果是横向使用android:paddingTop、android:paddingBottom，否则使用
    android:paddingLeft、android:paddingRight，来实现item与整个TabView的起始键距、与指示器之间的间距
```
#### 4.同文字等宽的线条指示器
```
  <i.farmer.widget.recyclerview.tabs.RecyclerTabView
    android:id="@+id/mTabView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingTop="@dimen/x20"
    android:paddingBottom="@dimen/x10"
    app:indicatorColor="#1A1A1A"
    app:indicatorHeight="@dimen/x10"
    app:indicatorPadding="@dimen/x30"
    app:indicatorStyle="fullLine"
    app:tabItemSpacing="@dimen/x40"
    app:tabPadding="@dimen/x60" />
```
#### 5.三角形指示器，可选在滑动过程中是否使用圆形指示器（考虑到三角形底部必须跟ViewPager挨着，但是ViewPager两页中间可能会有间隙，导致指示器同ViewPager分离的情况）
```
  <i.farmer.widget.recyclerview.tabs.RecyclerTabView
    android:id="@+id/mTabView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingTop="@dimen/x20"
    android:paddingBottom="@dimen/x10"
    app:indicatorColor="#1A1A1A"
    app:indicatorHeight="@dimen/x10"
    app:indicatorIncludeSpacing="true"
    app:indicatorSmoothCircle="true"
    app:indicatorStyle="triangle"
    app:indicatorWidth="@dimen/x28"
    app:tabItemSpacing="@dimen/x40"
    app:tabPadding="@dimen/x60" />
```
#### 6.块状背景指示器（圆角矩形）
```
  <i.farmer.widget.recyclerview.tabs.RecyclerTabView
    android:id="@+id/mTabView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingVertical="@dimen/x20"
    app:indicatorColor="#991A1A1A"
    app:indicatorPadding="@dimen/x30"
    app:indicatorStyle="block"
    app:tabItemSpacing="@dimen/x40"
    app:tabPadding="@dimen/x60" />
```
#### 7.属性说明
```
  a.指示器方向
    android:orientation 默认横向 horizontal｜vertical
  b.指示器类型 indicatorStyle
    none        需要自定义指示器，需继承i.farmer.widget.recyclerview.tabs.RecyclerTabViewIndicator
    line        线条指示器 (默认)
    fullLine    同tabItem等宽线条指示器（可左右增加indicatorPadding长度）
    triangle    三角形指示器（滑动过程中，可选变化为圆形指示器）
    block       块状背景指示器（现在为圆角）
  c.指示器颜色
    indicatorColor
  d.指示器宽度
    indicatorWidth  （在横向时，为指示器长度，竖向时为指示器厚度）
    （块状指示器无效，等宽线条指示器横向时无效）
  e.指示器高度
    indicatorHeight （在横向时，为指示器厚度，竖向时为指示器长度），其中fullLine时，不需要传入指示器长度
    （块状指示器无效，等宽线条指示器竖向时无效）
  f.指示器移动过程中是否有差值变化（三角形指示器无效）
    indicatorGap    默认有 never｜always
  g.block、fullLine指示器 是否给等宽指示器增加左右两边长度
    indicatorPadding  不能小于0，不能大于tabItemSpacing，默认为0
  h.移动过程中，是否使用圆形指示器，只有在三角形指示器的时候有小
    indicatorSmoothCircle 默认为false，true｜false
  i.tabItem间距
    tabItemSpacing
  j.整个RecyclerTabView容器开始、结束间距
    tabPaddingStart｜tabPaddingEnd｜tabPadding
    优先级 tabPaddingStart>tabPadding  tabPaddingEnd>tabPadding
```

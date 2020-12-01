# RecyclerView

## 指示器
```
支持三角形、圆形、圆角矩形，默认三角形

1.创建构造器
  new IndicatorDecoration.Builder()
    或者
  new IndicatorDecoration.Builder(context)  使用资源参数的时候，请传入Context上下文
2.指示器样式
  Builder.setShape(@ShapeMode int shape) 静止样式、滚动样式使用同一个
    或者
  Builder.setShape(@ShapeMode int staticShape, @ShapeMode int scrollShape) 静止样式、滚动样式分别设置
3.指示器颜色
  Builder.setColor(int color)
    或者
  Builder.setColorRes(@ColorRes int color)
4.指示器大小
  Builder.setSize(int width)  高度为宽度的一半
    或者
  Builder.setSize(int width, int height)
    或者
  Builder.setSizeRes(@DimenRes int width)
    或者
  Builder.setSizeRes(@DimenRes int width, @DimenRes int height)
5.指示器同itemView之间的间距
  Builder.setIndicatorPadding(int padding)
    或者
  Builder.setIndicatorPaddingRes(@DimenRes int padding) 
6.itemView之间的间距，默认为0
  Builder.setItemSpacing(int itemSpacing)
    或者
  Builder.setItemSpacingRes(@DimenRes int itemSpacing)
7.整个列表的前后padding
  Builder.setPadding(int padding)   前后间距相同
    或者
  Builder.setPaddingRes(@DimenRes int padding)
    或者
  Builder.setPaddingStart(int start)
    或者
  Builder.setPaddingStartRes(@DimenRes int start)
    或者
  Builder.setPaddingEnd(int end)
    或者
  Builder.setPaddingEndRes(@DimenRes int end) 
8.完整示例
new IndicatorDecoration.Builder(this)
  .setShape(IndicatorDecoration.SHAPE_OVAL)
  .setColorRes(R.color.colorPrimary)          // 使用Res资源，必须使用构造器传入Context
  .setSizeRes(R.dimen.x22, R.dimen.x10)
  .setIndicatorPaddingRes(R.dimen.x10)
  .setItemSpacingRes(R.dimen.x16)
  .setPadingRes(R.dimen.x30)
  .attach(RecycerView view, ViewPager pager);//  .attach(RecycerView view, ViewPager2 pager);

new IndicatorDecoration.Builder()
  .setShape(IndicatorDecoration.SHAPE_OVAL)
  .setColor(Color.parseColor("#1A1A1A"))
  .setSize(22, 10)
  .setIndicatorPadding(10)
  .setItemSpacing(16)
  .setPading(30)
  .attach(RecycerView view, ViewPager pager);  //  .attach(RecycerView view, ViewPager2 pager);
  以上两种方式可以交叉使用，其中指示器高度，可以不传入，默认为宽度的一半

```
```
注意：目前不支持 smooth，即ViewPager.setCurrentItem(position)，请使用 ViewPager.setCurrentItem(position, false)
```

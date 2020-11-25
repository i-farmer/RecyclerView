# RecyclerView

## 三角形指示器
```
1.创建方式一
new TriangleIndicatorDecoration.Builder()
  .setColor(Color.parseColor("#1A1A1A"))
  .setWidth(22)
  .setHeight(10)
  .setIndicatorPadding(10)
  .setItemSpacing(30)
  .build();
2.创建方式二
new TriangleIndicatorDecoration.Builder(this)
  .setColorRes(R.color.colorPrimary)          // 使用Res资源，必须使用构造器传入Context
  .setWidthRes(R.dimen.x22)
  .setHeightRes(R.dimen.x10)
  .setIndicatorPaddingRes(R.dimen.x10)
  .setItemSpacingRes(R.dimen.x30)
  .build();
3.创建方式三
  方式一、方式二可以交叉使用，其中指示器高度，可以不传入，默认为宽度的一半
4.绑定RecyclerView
  RecyclerView.addItemDecoration方法
5.绑定 androidx.viewpager.widget.ViewPager、或者androidx.viewpager2.widget.ViewPager2
  TriangleIndicatorDecoration.attach
```

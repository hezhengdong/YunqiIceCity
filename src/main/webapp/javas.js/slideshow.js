var logo = document.querySelector(".logo");
var wave = document.querySelector(".wave");
var ul = document.querySelector("#mylis");
var waves = wave.querySelectorAll("div");
var lis = document.getElementById("mylis").getElementsByTagName("li");
var divs = document
  .getElementsByClassName("shift")[0]
  .getElementsByTagName("div");
//用于点击下方圆点时切换图片并让目标圆点变黑
function shift(position) {
  let flag = 0;
  let realMove = -position * 100;
  for (flag; flag < 4; flag++) {
    lis[flag].style.transform = "translateX(" + realMove + "%)";
    divs[flag].style.backgroundColor = "white";
  }
  divs[position].style.backgroundColor = "black";
}
let flag = 0; //用于判断该轮到哪个li
let realflag = null,
  timer = null,
  realMove = null;
let flag1 = 0;
let innerFlag = 0;
let judge = null; //用于判断左右箭头的切换逻辑
let judge1 = 0,
  judge2 = 0,
  realjudge = 0; //用于点击左右箭头后循环移动四个li,用于判断出点击箭头时是哪个li后该往左或右移动几个身位,用于用于判断出点击箭头时是哪个li后该往左右移动几个百分比身位
let time = 2000; //轮播图切换时间
//用于隐藏首页波浪动画，显示轮播图的盒子
let switch5 = function () {
  wave.style.display = "none";
  ul.style.display = "block";
  // document.getElementById(".select").style.backgroundColor = "transparent";
};
//显示最下面的波浪
let switch1 = function () {
  waves[2].style.display = "block";
};
//显示中间的波浪
let switch2 = function () {
  waves[1].style.display = "block";
};
//显示最上面波浪
let switch3 = function () {
  waves[0].style.display = "block";
};
let switch4 = function () {
  logo.style.display = "block";
};
//显示logo
setTimeout(switch1, 1000);
setTimeout(switch2, 2000);
setTimeout(switch3, 3000);
setTimeout(switch4, 4000);
setTimeout(switch5, 5000);
//用于每次点击下方黑点切换时消除所有定时器
let clear = function () {
  clearInterval(timer);
  timer = null;
};
//用于判断flag这个变量如何变化的函数
let flagLogic = function () {
  if (flag == 3) {
    flag = 0;
    innerFlag = 0;
  } else {
    flag++;
    innerFlag = 0;
  }
};
//用于左箭头判断flag这个变量如何变化的函数
let flagLogicReverse = function () {
  if (flag == 0) {
    flag = 3;
    innerFlag = 0;
  } else {
    flag--;
    innerFlag = 0;
  }
};
//用于每次定时器每次自动切换时把所有圆点变为白色并让目标圆点变黑
let buttonShift = function () {
  for (flag1; flag1 < 4; flag1++) {
    divs[flag1].style.backgroundColor = "white";
  }
  flag1 = 0;
  divs[flag].style.backgroundColor = "black";
};
//判断点击黑点后的逻辑
let condition = function () {
  simplification1();
  buttonShift();
  if (flag == 3) {
    flag = 0;
    innerFlag = 0;
  } else {
    flag++;
    innerFlag = 0;
  }
};
//用于精简函数的for循环
let simplification = function () {
  for (judge1; judge1 < 4; judge1++) {
    lis[judge1].style.transform = "translateX(" + realjudge + "%)";
  }
};
//用于精简点击黑点后轮播图逻辑函数
let simplification1 = function () {
  realMove = -flag * 100;
  for (innerFlag; innerFlag < 4; innerFlag++) {
    lis[innerFlag].style.transform = "translateX(" + realMove + "%)";
  }
};
//用于精简箭头切换函数
let simplification2 = function () {
  buttonShift();
  flagLogic();
  realjudge = -judge2 * 100;
  simplification();
};
//用于判断右箭头切换的函数
let right = function () {
  clear();
  judge = lis[1].style.transform;
  if (judge == "translateX(0%)") {
    flag = 1;
    judge1 = 0;
    judge2 = 1;
    simplification2();
  } else if (judge == "translateX(-100%)") {
    flag = 2;
    judge1 = 0;
    judge2 = 2;
    simplification2();
  } else if (judge == "translateX(-200%)") {
    flag = 3;
    judge1 = 0;
    judge2 = 3;
    simplification2();
  } else if (judge == "translateX(-300%)") {
    flag = 0;
    judge1 = 0;
    judge2 = 0;
    simplification2();
  } else {
    flag = 1;
    judge1 = 0;
    judge2 = 1;
    simplification2();
  }
};
//用于判断左箭头切换的函数
let left = function () {
  clear();
  judge = lis[1].style.transform;
  if (judge == "translateX(0%)") {
    flag = 3;
    judge1 = 0;
    judge2 = 3;
    simplification2();
  } else if (judge == "translateX(-100%)") {
    flag = 0;
    judge1 = 0;
    judge2 = 0;
    simplification2();
  } else if (judge == "translateX(-200%)") {
    flag = 1;
    judge1 = 0;
    judge2 = 1;
    simplification2();
  } else if (judge == "translateX(-300%)") {
    flag = 2;
    judge1 = 0;
    judge2 = 2;
    simplification2();
  } else {
    flag = 3;
    judge1 = 0;
    judge2 = 3;
    simplification2();
  }
};
automaticShift(1); //让轮播图初始是动的
//点击黑点的轮播图逻辑
function automaticShift(number) {
  {
    flag = number;
    clear();
    timer = setInterval(condition, time);
  }
}
import { request } from "../../request/index.js";
var app = getApp();
var date = new Date();
const years = [];
const months = [];
const days = [];
const hours = [];
const minutes = [];
//获取年
for (let i = date.getFullYear(); i <= date.getFullYear() + 5; i++) {
  years.push("" + i);
}
//获取月份
for (let i = 1; i <= 12; i++) {
  if (i < 10) {
    i = "0" + i;
  }
  months.push("" + i);
}
//获取日期
for (let i = 1; i <= 31; i++) {
  if (i < 10) {
    i = "0" + i;
  }
  days.push("" + i);
}
//获取小时
for (let i = 0; i < 24; i++) {
  if (i < 10) {
    i = "0" + i;
  }
  hours.push("" + i);
}
//获取分钟
for (let i = 0; i < 60; i++) {
  if (i < 10) {
    i = "0" + i;
  }
  minutes.push("" + i);
}
Page({
  data: {
    id: "",
    time: "",
    multiArray: [years, months, days, hours, minutes],
    multiIndex: [],
    choose_year: "",
    input: "",
  },
  onLoad: function (option) {
    if (option.id) {
      this.setData({
        id: option.id,
      });
      const id = option.id;
      request({
        url: app.globalData.baseUrl + "/plan/detail/" + id,
      }).then((res) => {
        this.setData({
          time: res.data.data.jobTime,
          input: res.data.data.jobDescription,
        });
        wx.setStorageSync("time", res.data.data.jobTime);
        wx.setStorageSync("input", res.data.data.jobDescription);
      });
    }
  },
  onShow: function () {
    var cur = new Date();
    date = new Date(cur.setMinutes(cur.getMinutes() + 5));
    this.setData({
      multiIndex: [
        0,
        date.getMonth(),
        date.getDate() - 1,
        date.getHours(),
        date.getMinutes(),
      ],
    });
  },
  onUnload: function(){
    wx.setStorageSync("input", '');
    wx.setStorageSync("time",'');
  },
  contentInput: function (e) {
    this.setData({
      input: e.detail.value,
    });
    wx.setStorageSync("input", e.detail.value);
  },
  //获取时间日期
  bindMultiPickerChange: function (e) {
    // console.log('picker发送选择改变，携带值为', e.detail.value)
    this.setData({
      multiIndex: e.detail.value,
    });
    const index = this.data.multiIndex;
    const year = this.data.multiArray[0][index[0]];
    const month = this.data.multiArray[1][index[1]];
    const day = this.data.multiArray[2][index[2]];
    const hour = this.data.multiArray[3][index[3]];
    const minute = this.data.multiArray[4][index[4]];
    this.setData({
      time: year + "-" + month + "-" + day + " " + hour + ":" + minute,
    });
    wx.setStorageSync("time", this.data.time);

    // console.log(this.data.time);
  },
  //监听picker的滚动事件
  bindMultiPickerColumnChange: function (e) {
    //获取年份
    if (e.detail.column == 0) {
      let choose_year = this.data.multiArray[e.detail.column][e.detail.value];
      //console.log(choose_year);
      this.setData({
        choose_year,
      });
    }
    //console.log('修改的列为', e.detail.column, '，值为', e.detail.value);
    if (e.detail.column == 1) {
      let num = parseInt(this.data.multiArray[e.detail.column][e.detail.value]);
      let temp = [];
      if (
        num == 1 ||
        num == 3 ||
        num == 5 ||
        num == 7 ||
        num == 8 ||
        num == 10 ||
        num == 12
      ) {
        //判断31天的月份
        for (let i = 1; i <= 31; i++) {
          if (i < 10) {
            i = "0" + i;
          }
          temp.push("" + i);
        }
        this.setData({
          ["multiArray[2]"]: temp,
        });
      } else if (num == 4 || num == 6 || num == 9 || num == 11) {
        //判断30天的月份
        for (let i = 1; i <= 30; i++) {
          if (i < 10) {
            i = "0" + i;
          }
          temp.push("" + i);
        }
        this.setData({
          ["multiArray[2]"]: temp,
        });
      } else if (num == 2) {
        //判断2月份天数
        let year = parseInt(this.data.choose_year);
        if ((year % 400 == 0 || year % 100 != 0) && year % 4 == 0) {
          for (let i = 1; i <= 29; i++) {
            if (i < 10) {
              i = "0" + i;
            }
            temp.push("" + i);
          }
          this.setData({
            ["multiArray[2]"]: temp,
          });
        } else {
          for (let i = 1; i <= 28; i++) {
            if (i < 10) {
              i = "0" + i;
            }
            temp.push("" + i);
          }
          this.setData({
            ["multiArray[2]"]: temp,
          });
        }
      }
      //console.log(this.data.multiArray[2]);
    }
    var data = {
      multiArray: this.data.multiArray,
      multiIndex: this.data.multiIndex,
    };
    data.multiIndex[e.detail.column] = e.detail.value;
    this.setData(data);
  },
  handleBtn: () => {
    wx.requestSubscribeMessage({
      tmplIds: ["baL1uvWBmJ668_EYGKm7Q6JXiOw-7oAi0QvZnGgH2J8"], // 此处可填写多个模板 ID，但低版本微信不兼容只能授权一个
      success: () => {
        var pages = getCurrentPages(); //获取加载的页面
        var currentPage = pages[pages.length - 1]; //获取当前页面的对象

        const token = wx.getStorageSync("token");
        const time = wx.getStorageSync("time");
        const input = wx.getStorageSync("input");

        const id = currentPage.data.id;
        if (id != "") {
          request({
            url: app.globalData.baseUrl + "/plan/edit/" + id,
            data: {
              time,
              input,
            },
          }).then((res) => {
            wx.setStorageSync("time", "");
            wx.setStorageSync("input", "");

            currentPage.setData({
              input: "",
              time: "",
            });
            wx.showModal({
              title: "提示",
              content: res.data.msg,
              showCancel: false,
            });
          });
        } else {
          request({
            method: "post",
            url: app.globalData.baseUrl + "/plan/create",
            data: {
              token,
              time,
              input,
            },
          }).then((res) => {
            wx.setStorageSync("time", "");
            wx.setStorageSync("input", "");

            currentPage.setData({
              input: "",
              time: "",
            });
            wx.showModal({
              title: "提示",
              content: res.data.msg,
              showCancel: false,
            });
          });
        }
      },
      fail: (res) => {
        console.log(res.errCode);
        console.log(res.errMsg);
      },
    });
  },
});

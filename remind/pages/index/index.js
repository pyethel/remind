import { request } from "../../request/index.js";
var app = getApp();
Page({
  data: {
    List: [],
  },
  onShow: function () {
    wx.login({
      success: (res) => {
        if (res.code) {
          request({
            url: app.globalData.baseUrl + "/app/login",
            data: {
              code: res.code,
            },
          }).then((res) => {
            wx.setStorageSync("token", res.data.data);
          });
        }
      },
    });
    const token = wx.getStorageSync("token");
    if (token) {
      request({
        url: app.globalData.baseUrl + "/plan/list",
        data: {
          token: token,
        },
      }).then((res) => {
        this.setData({
          List: res.data.data,
        });
      });
    }
  },
  /**
   * 创建按钮点击事件
   */
  handleCreate: () => {
    wx.navigateTo({
      url: "../create/create",
    });
  },
  /**
   * 删除按钮点击事件
   */
  handleDelete: (event) => {
    wx.showModal({
      title: "提示",
      content: "确认删除？",
      success: (res) => {
        if (res.confirm) {
          const id = event.currentTarget.dataset.id;
          request({
            url: app.globalData.baseUrl + "/plan/delete",
            data: {
              id,
            },
          }).then(() => {
            var pages = getCurrentPages(); //获取加载的页面
            var currentPage = pages[pages.length - 1]; //获取当前页面的对象
            currentPage.onShow();
            wx.showToast({
              title: "删除成功",
              icon: "success",
              duration: 2000,
            });
          });
        }
      },
    });
  },
  /**
   * 编辑按钮点击事件
   */
  handleEdit: (event) => {
    const id = event.currentTarget.dataset.id;
    wx.navigateTo({
      url: "../create/create?id=" + id,
    });
  },
});

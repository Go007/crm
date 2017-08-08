/**
 * Created by John on 2017/8/4.
 * 闭包
 */
/*
 * 使用原型创建string拼接方法
 * */
var myString = (function () {
    function sayHello(name){
        console.log(name);
    }

    function StringBuffer() {
        this.__strings__ = new Array();
    }
    StringBuffer.prototype.append = function (str) {
        this.__strings__.push(str);
        return this;    //方便链式操作
    }

    StringBuffer.prototype.toString = function () {
        return this.__strings__.join("");
    }

    return{
        getStringBuffer:new StringBuffer(),
        sayHello:sayHello
    }
})();

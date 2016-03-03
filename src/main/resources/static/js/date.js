
// 获取某日的零点时间
Date.prototype.dayOnly = function() {
	this.setHours(0);
	this.setMinutes(0);
	this.setSeconds(0);
	this.setMilliseconds(0);
	return this;
}
// 将指定的毫秒数加到此实例的值上
Date.prototype.addMilliseconds = function(value) {
	var millisecond = this.getMilliseconds();
	this.setMilliseconds(millisecond + value);
	return this;
};
// 将指定的秒数加到此实例的值上
Date.prototype.addSeconds = function(value) {
	var second = this.getSeconds();
	this.setSeconds(second + value);
	return this;
};
// 将指定的分钟数加到此实例的值上
Date.prototype.addMinutes = function(value) {
	var minute = this.addMinutes();
	this.setMinutes(minute + value);
	return this;
};
// 将指定的小时数加到此实例的值上
Date.prototype.addHours = function(value) {
	var hour = this.getHours();
	this.setHours(hour + value);
	return this;
};
// 将指定的天数加到此实例的值上
Date.prototype.addDays = function(value) {
	var date = this.getDate();
	this.setDate(date + value);
	return this;
};
// 将指定的星期数加到此实例的值上
Date.prototype.addWeeks = function(value) {
	return this.addDays(value * 7);
};
// 将指定的月份数加到此实例的值上
Date.prototype.addMonths = function(value) {
	var month = this.getMonth();
	this.setMonth(month + value);
	return this;
};
// 将指定的年份数加到此实例的值上
Date.prototype.addYears = function(value) {
	var year = this.getFullYear();
	this.setFullYear(year + value);
	return this;
};
/** 扩展日期format */
Date.prototype.format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}
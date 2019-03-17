<html>
<head>
	<title>测试页面</title>
</head>
<body>
	学生信息：<br>
	学号：${student.id}<br>
	姓名：${student.name}<br>
	年龄：${student.age}<br>
	家庭住址：${student.address}<br>
	学生列表：<br>
	<table border="1">
		<tr>
			<th>序号</th>
			<th>学号</th>
			<th>姓名</th>
			<th>年龄</th>
			<th>家庭住址</th>
		</tr>
		<#list stuList as student>
		<#if student_index%2==0>
			<tr bgcolor="red">
		<#else>
			<tr bgcolor="green">
		</#if>
			<th>${student_index}</th>
			<th>${student.id}</th>
			<th>${student.name}</th>
			<th>${student.age}</th>
			<th>${student.address}</th>
		</tr>
		</#list>
	</table>
	<br>
	日期类型的处理：${date?string('dd/mm/yyyy HH:mm:ss')}
	<br>
	null值的处理：${val!"默认"}
	<#if val??>
		val有值
	<#else>
		val为null
	</#if>
	<br>
	include标签测试：
	<#include "hello.ftl">
</body>
</html>
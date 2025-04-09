<%--
  Created by IntelliJ IDEA.
  User: kavisarna
  Date: 4/9/25
  Time: 2:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <title>TMDB API Dashboard</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 20px;
      background-color: #f5f5f5;
    }
    h1 {
      color: #333;
      text-align: center;
    }
    h2 {
      color: #444;
      margin-top: 30px;
    }
    .analytics-container {
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
      margin-bottom: 30px;
    }
    .metric-card {
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
      padding: 20px;
      margin: 10px;
      flex: 1;
      min-width: 200px;
      text-align: center;
    }
    .metric-card h3 {
      margin-top: 0;
      color: #555;
    }
    .metric-card p {
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 0;
      color: #2c75ea;
    }
    .logs-table {
      width: 100%;
      border-collapse: collapse;
      background-color: white;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
      border-radius: 8px;
      overflow: hidden;
    }
    .logs-table th {
      background-color: #2c75ea;
      color: white;
      padding: 12px;
      text-align: left;
    }
    .logs-table td {
      padding: 10px 12px;
      border-top: 1px solid #eee;
    }
    .logs-table tr:hover {
      background-color: #f9f9f9;
    }
    .status-success {
      color: green;
      font-weight: bold;
    }
    .status-error {
      color: red;
      font-weight: bold;
    }
    .json-content {
      max-width: 300px;
      max-height: 100px;
      overflow: auto;
      font-family: monospace;
      font-size: 12px;
      white-space: pre-wrap;
      word-break: break-word;
      background-color: #f8f9fa;
      padding: 4px;
      border-radius: 4px;
      border: 1px solid #ddd;
    }
    /* Tooltip style for hover interaction */
    .json-tooltip {
      position: relative;
      display: inline-block;
    }
    .json-tooltip .tooltip-text {
      visibility: hidden;
      width: 80px;
      background-color: #555;
      color: #fff;
      text-align: center;
      border-radius: 6px;
      padding: 5px;
      position: absolute;
      z-index: 1;
      bottom: 125%;
      left: 50%;
      margin-left: -40px;
      opacity: 0;
      transition: opacity 0.3s;
      font-size: 12px;
    }
    .json-tooltip:hover .tooltip-text {
      visibility: visible;
      opacity: 1;
    }
  </style>
</head>
<body>
<h1>TMDB API Dashboard</h1>

<h2>Operations Analytics</h2>

<div class="analytics-container">
  <div class="metric-card">
    <h3>Total Requests</h3>
    <p>${totalRequests}</p>
  </div>

  <div class="metric-card">
    <h3>Average Response Time</h3>
    <p><fmt:formatNumber value="${averageTmdbResponseTime}" pattern="#,##0.00" /> ms</p>
  </div>

  <div class="metric-card">
    <h3>Most Popular Search</h3>
    <p>${topQueryKey}</p>
    <small>(${topQueryValue} searches)</small>
  </div>
</div>

<h2>Request Logs</h2>
<table class="logs-table">
  <tr>
    <th>Time</th>
    <th>Client</th>
    <th>Method</th>
    <th>Endpoint</th>
    <th>Query</th>
    <th>Response Time</th>
    <th>Response JSON</th>
    <th>Status</th>
  </tr>
  <c:forEach items="${logs}" var="log">
    <tr>
      <td><fmt:formatDate value="${log.timestamp}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
      <td>${log.clientIp}</td>
      <td>${log.requestMethod}</td>
      <td>${log.endpoint}</td>
      <td>${log.queryParams}</td>
      <td>${log.responseTime} ms</td>
      <td>
        <div class="json-tooltip">
          <div class="json-content">${log.tmdbResponseJSON}</div>
          <span class="tooltip-text">Scroll to see more</span>
        </div>
      </td>
      <td class="${log.statusCode == 200 ? 'status-success' : 'status-error'}">
          ${log.statusCode}
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty logs}">
    <tr>
      <td colspan="8" style="text-align: center; padding: 20px;">No logs found</td>
    </tr>
  </c:if>
</table>
</body>
</html>
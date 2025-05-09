<%@ include file="/include.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${buildScans.size()>0}">
        <ul style="margin-top:0;padding:0">
            <c:forEach items="${buildScans.all()}" var="buildScan">
                <li style="list-style: none"><a href="${buildScan.url}" target="_blank"><img
                    src="${buildScan.buildScanBadge}" alt="${buildScan.urlWithoutProtocol}"></a></li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <c:if test="${hasSupportedRunner}">
            No Build Scan published.
        </c:if>
    </c:otherwise>
</c:choose>

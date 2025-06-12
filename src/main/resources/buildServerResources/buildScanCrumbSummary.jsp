<%@ include file="/include.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${isPublished}">
        <ul style="margin-top:0;padding:0">
            <c:forEach items="${buildScans.all()}" var="buildScan">
                <li style="list-style: none"><a href="${buildScan.url}" target="_blank"><img
                        src="${buildScan.publishedBuildScanBadge}" alt="${buildScan.urlWithoutProtocol}"></a></li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <ul style="margin-top:0;padding:0">
            <c:if test="${hasSupportedRunner}">
                <li style="list-style: none"><a href="${buildScan.url}" target="_blank"><img
                        src="${buildScan.notPublishedBuildScanBadge}" alt="${buildScan.urlWithoutProtocol}"></a></li>
            </c:if>
        </ul>
    </c:otherwise>
</c:choose>

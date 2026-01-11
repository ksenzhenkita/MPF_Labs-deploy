<#include "layout/header.ftl">
<h3>Нова книга додана до каталогу</h3>


<p>Назва: <b>${title}</b></p>
<p>Автор: <b>${author}</b></p>

<#assign yearClean = year?replace("[^0-9]", "", "r")>
<#if yearClean?number < 2000>
    <p style="color:#7a4e00"><b>Раритетне видання!</b></p>
</#if>

<#if comments?? && comments?size > 0>
    <p>Коментарі:</p>
    <ul>
        <#list comments as c>
            <li>${c}</li>
        </#list>
    </ul>
</#if>
<a href="${appBaseUrl}/comments?bookId=${id?if_exists}"
   style="background:#4CAF50;color:white;padding:10px 15px;text-decoration:none;border-radius:5px;">
    Переглянути книгу
</a>

<p>Дата додавання: ${createdAt?string("yyyy-MM-dd HH:mm")}</p>
<#include "layout/footer.ftl">
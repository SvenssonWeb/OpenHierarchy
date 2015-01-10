<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

<!-- 	<xsl:variable name="scripts"> -->
<!-- 		/js/UserGroupList.js -->
<!-- 	</xsl:variable> -->

	<xsl:template name="UserList">
		<xsl:param name="name" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="users" select="null"/>
		<xsl:param name="connectorURL"/>
		<xsl:param name="placeholder" select="$i18n.SearchUsers"/>
		<xsl:param name="showEmail" select="false()"/>
	
		<ul class="list-style-type-none margintop usergroup-list" id="{$name}-user-list">
		
			<input type="hidden" name="prefix" disabled="disabled" value="{$name}"/>
			<input type="hidden" name="suffix" disabled="disabled" value="user"/>
			<input type="hidden" name="connectorURL" disabled="disabled" value="{$connectorURL}"/>
			
			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:apply-templates select="$requestparameters/parameter[name=concat($name,'-user')]/value" mode="user">
						<xsl:with-param name="requestparameters" select="$requestparameters"/>
						<xsl:with-param name="prefix" select="$name"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$users/user" mode="ajaxlist">
						<xsl:with-param name="prefix" select="$name"/>
						<xsl:with-param name="showEmail" select="$showEmail"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
					
			
					
			<li id="{$name}-user-template" class="hidden show-email-{$showEmail}">
				
				<input type="hidden" name="{$name}-user" disabled="disabled"/>
				<input type="hidden" name="{$name}-username" disabled="disabled"/>

				<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png" alt="" />
				
				<span class="text"/>

				<div class="floatright">
					<a class="delete" href="#" title="{$i18n.DeleteUser}:">
						<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete.png" alt="{$i18n.DeleteUser}" />
					</a>
				</div>
			</li>
			
		</ul>
		
		<xsl:if test="$connectorURL">
				
			<div class="ui-widget">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id">
						<xsl:value-of select="$name"/>
						<xsl:value-of select="'-search-user'"/>
					</xsl:with-param>
					<xsl:with-param name="class" select="'full border-box'"/>
					<xsl:with-param name="width" select="''"/>
					<xsl:with-param name="placeholder" select="$placeholder"/>
				</xsl:call-template>
			</div>
		
		</xsl:if>
	
		<br/>
		
	</xsl:template>
	
	<xsl:template name="ReadOnlyUserList">
	
		<xsl:param name="users" select="null" />
		<xsl:param name="showEmail" select="false()" />
	
		<ul class="list-style-type-none margintop readonly-usergroup-list">
			
			<xsl:apply-templates select="$users/user" mode="readonly">
				<xsl:with-param name="showEmail" select="$showEmail"/>
			</xsl:apply-templates>
			
		</ul>
	
	</xsl:template>
	
	<xsl:template match="value" mode="user">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="prefix"/>
	
		<xsl:variable name="userID" select="."/>
	
		<xsl:variable name="name" select="$requestparameters/parameter[name=concat($prefix,'-username',$userID)]/value"/>
		
		<xsl:if test="$name != ''">
	
			<li id="{prefix}-user_{.}" class="{$prefix}-user-list-entry">
				
				<input type="hidden" name="{$prefix}-user" value="{.}"/>
				<input type="hidden" name="{$prefix}-username{.}" value="{$name}"/>
				
				<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png" alt="" />
				
				<span class="text">
					<xsl:value-of select="$name"/>	
				</span>			
				
				<div class="floatright">
					<a class="delete" href="#" title="{$i18n.DeleteUser}: {$name}">
						<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete.png" alt="{$i18n.DeleteUser}" />
					</a>
				</div>
			</li>
		
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template match="user" mode="ajaxlist">
	
		<xsl:param name="prefix"/>
		<xsl:param name="showEmail" />
	
		<xsl:variable name="name">
		
			<xsl:value-of select="firstname"/>
			
			<xsl:text>&#x20;</xsl:text>
			
			<xsl:value-of select="lastname"/>
			
			<xsl:if test="username">
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:text>(</xsl:text>
					<xsl:value-of select="username"/>
				<xsl:text>)</xsl:text>
									
			</xsl:if>
					
			<xsl:if test="$showEmail and email">
				
				<xsl:text>,&#x20;</xsl:text>
				
				<xsl:value-of select="email" />
				
			</xsl:if>
					
		</xsl:variable>
	
		<li id="{$prefix}-user_{userID}" class="{$prefix}-user-list-entry">
			
			<input type="hidden" name="{$prefix}-user" value="{userID}"/>
			<input type="hidden" name="{$prefix}-username{userID}" value="{$name}"/>
			
			<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png" alt="" />
			
			<span class="text">
				<xsl:value-of select="$name"/>
			</span>			
			
			<div class="floatright">
				<a class="delete" href="#" title="{$i18n.DeleteUser}: {$name}">
					<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user_delete.png" alt="{$i18n.DeleteUser}" />
				</a>
			</div>
		</li>
	
	</xsl:template>	
	
	<xsl:template match="user" mode="readonly">
	
		<xsl:param name="showEmail" />
	
		<li>
			
			<xsl:variable name="name">
		
				<xsl:value-of select="firstname"/>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:value-of select="lastname"/>
				
				<xsl:if test="username">
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:text>(</xsl:text>
						<xsl:value-of select="username"/>
					<xsl:text>)</xsl:text>
										
				</xsl:if>
					
			</xsl:variable>
			
			<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png" alt="" />
			
			<span class="text">
				
				<xsl:value-of select="$name"/>
				
				<xsl:if test="$showEmail and email">
					
					<xsl:text>,&#x20;</xsl:text>
					<a href="mailto:{email}" title="{$i18n.SendMailTo}: {email}"><xsl:value-of select="email" /></a>
					
				</xsl:if>
				
			</span>
			
		</li>
	
	</xsl:template>
	
	<xsl:template name="GroupList">
		<xsl:param name="name" select="null"/>
		<xsl:param name="requestparameters" select="requestparameters"/>
		<xsl:param name="groups" select="null"/>
		<xsl:param name="connectorURL"/>
	
		<ul class="list-style-type-none margintop usergroup-list" id="{$name}-group-list">
		
			<input type="hidden" name="prefix" disabled="disabled" value="{$name}"/>
			<input type="hidden" name="suffix" disabled="disabled" value="group"/>
			<input type="hidden" name="connectorURL" disabled="disabled" value="{$connectorURL}"/>
			
			<xsl:choose>
				<xsl:when test="$requestparameters">
					<xsl:apply-templates select="$requestparameters/parameter[name=concat($name,'-group')]/value" mode="group">
						<xsl:with-param name="requestparameters" select="$requestparameters"/>
						<xsl:with-param name="prefix" select="$name"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$groups/group" mode="ajaxlist">
						<xsl:with-param name="prefix" select="$name"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
					
			<li id="{$name}-group-template" class="hidden">
				
				<input type="hidden" name="{$name}-group" disabled="disabled"/>
				<input type="hidden" name="{$name}-groupname" disabled="disabled"/>

				<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group.png" alt="" />
				
				<span class="text"/>

				<div class="floatright">
					<a class="delete" href="#" title="{$i18n.DeleteGroup}:">
						<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_delete.png" alt="{$i18n.DeleteGroup}" />
					</a>
				</div>
			</li>
			
		</ul>
		
		<xsl:if test="$connectorURL">
		
			<div class="ui-widget">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id">
						<xsl:value-of select="$name"/>
						<xsl:value-of select="'-search-group'"/>
					</xsl:with-param>
					<xsl:with-param name="class" select="'full border-box'"/>
					<xsl:with-param name="width" select="''"/>
					<xsl:with-param name="placeholder" select="$i18n.SearchGroups"/>
				</xsl:call-template>
			</div>
		
		</xsl:if>
	
		<br/>
		
	</xsl:template>
	
	<xsl:template name="ReadOnlyGroupList">
	
		<xsl:param name="groups" select="null" />
	
		<ul class="list-style-type-none margintop readonly-usergroup-list">
			
			<xsl:apply-templates select="$groups/group" mode="readonly" />
			
		</ul>
	
	</xsl:template>
	
	<xsl:template match="value" mode="group">
	
		<xsl:param name="requestparameters"/>
		<xsl:param name="prefix"/>
	
		<xsl:variable name="groupID" select="."/>
	
		<xsl:variable name="name" select="$requestparameters/parameter[name=concat($prefix,'-groupname',$groupID)]/value"/>
	
		<li id="{prefix}-group_{.}" class="{$prefix}-group-list-entry">
			
			<input type="hidden" name="{$prefix}-group" value="{.}"/>
			<input type="hidden" name="{$prefix}-groupname{.}" value="{$name}"/>
			
			<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group.png" alt="" />
			
			<span class="text">
				<xsl:value-of select="$name"/>	
			</span>			
			
			<div class="floatright">
				<a class="delete" href="#" title="{$i18n.DeleteGroup}: {$name}">
					<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_delete.png" alt="{$i18n.DeleteGroup}" />
				</a>
			</div>
		</li>
	
	</xsl:template>
	
	<xsl:template match="group" mode="ajaxlist">
	
		<xsl:param name="prefix"/>
	
		<xsl:variable name="name">
		
			<xsl:value-of select="name"/>
			
			<xsl:if test="description">
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:text>(</xsl:text>
					<xsl:value-of select="description"/>
				<xsl:text>)</xsl:text>
									
			</xsl:if>
					
		</xsl:variable>
	
		<li id="{$prefix}-group_{groupID}" class="{$prefix}-group-list-entry">
			
			<input type="hidden" name="{$prefix}-group" value="{groupID}"/>
			<input type="hidden" name="{$prefix}-groupname{groupID}" value="{$name}"/>
			
			<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group.png" alt="" />
			
			<span class="text">
				<xsl:value-of select="$name"/>
			</span>			
			
			<div class="floatright">
				<a class="delete" href="#" title="{$i18n.DeleteGroup}: {$name}">
					<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/group_delete.png" alt="{$i18n.DeleteGroup}" />
				</a>
			</div>
		</li>
	
	</xsl:template>	
	
	<xsl:template match="group" mode="readonly">
	
		<li>
			
			<xsl:variable name="name">
		
				<xsl:variable name="name">
			
				<xsl:value-of select="name"/>
				
				<xsl:if test="description">
					
					<xsl:text>&#x20;</xsl:text>
					
					<xsl:text>(</xsl:text>
						<xsl:value-of select="description"/>
					<xsl:text>)</xsl:text>
										
				</xsl:if>
					
			</xsl:variable>
					
		</xsl:variable>
			
			<img class="vertical-align-middle" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/user.png" alt="" />
			
			<span class="text">
				<xsl:value-of select="$name"/>
			</span>
			
		</li>
	
	</xsl:template>
	
</xsl:stylesheet>


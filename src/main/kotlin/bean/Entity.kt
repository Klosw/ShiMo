package bean

/***
 * 请求获取文件夹数据
 */
class Folder : ArrayList<FolderItem>()

data class FolderItem(
    val fileSize: Long,
    val guid: String,
    val hasChildren: Boolean,
    val id: Long,
    val inviteCode: String,
    val isCloudFile: Boolean,
    val isDesktop: Boolean,
    val isFileAdmin: Boolean,
    val isFolder: Boolean,
    val isFromSVC: Boolean,
    val isLegacy: Boolean,
    val isLocked: Boolean,
    val isShortcut: Boolean,
    val isSpace: Boolean,
    val marked: Boolean,
    val name: String,
    val namePinyin: String,
    val parentId: Long,
    val parentRole: String,
    val passwordProtected: Boolean,
    val permissions: Permissions,
    val role: String,
    val shareCount: Int,
    val shareMode: String,
    val sortName: List<String>,
    val starred: Boolean,
    val tags: List<Any>,
    val teamId: Int,
    val type: String,
    val updatedAt: String,
    val updatedBy: Int,
    val url: String,
    val downloadUrl: String?,
)

data class Permissions(
    val adminManageable: Boolean,
    val canApplyPermission: Boolean,
    val canCustomizeAdvancedPermission: Boolean,
    val canDuplicate: Boolean,
    val canModifyAdvancedPermission: Boolean,
    val canPrint: Boolean,
    val canRename: Boolean,
    val canSaveAsTemplate: Boolean,
    val canUncompress: Boolean,
    val childFileCreatable: Boolean,
    val collaboratorManageable: Boolean,
    val commentable: Boolean,
    val copyable: Boolean,
    val downloadable: Boolean,
    val editable: Boolean,
    val exitable: Boolean,
    val exportable: Boolean,
    val fileAuthSetable: Boolean,
    val lockable: Boolean,
    val manageable: Boolean,
    val moveable: Boolean,
    val outsiderAddable: Boolean,
    val passwordShareable: Boolean,
    val readable: Boolean,
    val removable: Boolean,
    val shareModeManageable: Boolean,
    val sheetLockable: Boolean,
    val teamShareModeManageable: Boolean,
    val unlockable: Boolean,
)

data class MeEntity(
    val avatar: String,
    val createdAt: String,
    val editionName: String,
    val email: Any,
    val hasPassword: Boolean,
    val id: Int?,
    val isSeat: Int,
    val mergedInto: Any,
    val mobile: Any,
    val name: String,
    val requiresIdentityVerification: Boolean,
    val status: Int,
    val team: Team?,
    val teamId: Int,
    val teamRole: String,
    val teamTime: String,
)

data class Team(
    val city: Any,
    val createdAt: String,
    val deletedAt: Any,
    val dissolved: Boolean,
    val id: Int,
    val info: String,
    val isMerged: Boolean,
    val mobile: Any,
    val name: String,
    val province: Any,
    val quitAction: String,
    val scale: Any,
    val scaleNum: Int,
    val type: String,
    val updatedAt: String,
)

data class SpacesEntity(
    val spaces: MutableList<Space>?,
)

data class Space(
    val accountMetadata: AccountMetadata,
    val createdAt: Long,
    val downloadUrl: String,
    val fileSize: Long,
    val guid: String,
    val isAdmin: Boolean,
    val isCloudFile: Boolean,
    val isDesktop: Boolean,
    val isLegacy: Boolean,
    val isShortcut: Boolean,
    val name: String,
    val onlyAdminsCanModifyCollaborators: Boolean,
    val parentRole: String,
    val role: String,
    val sharedAt: Long,
    val shortcutSource: Any,
    val sortName: List<String>,
    val starred: Boolean,
    val subType: Int,
    val team: Team,
    val teamId: Int,
    val thumbnailUrl: String,
    val type: Int,
    val updatedAt: Long,
    val url: String,
    val views: Int,
)

data class AccountMetadata(
    val expiredAt: Int,
    val isDingtalk: Boolean,
    val isEnterprise: Boolean,
    val isEnterpriseLight: Boolean,
    val isEnterprisePremium: Boolean,
    val isExpired: Boolean,
    val isFreeEnterprise: Boolean,
    val isPersonalPremium: Boolean,
    val isTrial: Boolean,
    val isWework: Boolean,
)

data class ExportEntity(
    val errorCode: Int,
    val errorDetail: ErrorDetail,
    val message: String,
    val status: Int,
    val taskId: String?,
)

class ErrorDetail

data class ExDownLoadEntity(
    val code: Int,
    val `data`: Data,
    val error: Error,
    val status: Int,
)

data class Data(
    val costTime: Int,
    val downloadUrl: String?,
    val fileSize: Long,
    val progress: Int,
)

class Error

data class AncestorsEntity(
    val `data`: DataAncestor,
    val domain: String,
    val requestID: String,
)

data class DataAncestor(
    val ancestors: List<Ancestor>,
    val rootType: Int,
)

data class Ancestor(
    val guid: String,
    val id: Long,
    val name: String,
)

data class RedirectEntity(
    val redirectUrl: String?,
)
data class GuidEntity(
    val guid: String
)
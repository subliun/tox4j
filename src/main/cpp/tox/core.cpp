#include <tox/core.h>


void
new_tox_group_invite_friend(Tox *tox, int groupnumber, int32_t friendnumber)
{
  tox_group_invite_friend(tox, groupnumber, friendnumber);
}

/*
int
new_tox_group_toggle_ignore(Tox *tox, int groupnumber, uint32_t peernumber, uint8_t ignore, TOX_ERR_GROUP_SET *error);
*/
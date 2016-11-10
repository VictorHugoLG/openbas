import React, {Component, PropTypes} from 'react'
import {connect} from 'react-redux'
import {addUser} from '../../../../actions/User'
import {fetchOrganizations, addOrganization} from '../../../../actions/Organization'
import {Dialog} from '../../../../components/Dialog';
import {FlatButton} from '../../../../components/Button';
import UserForm from '../../admin/users/UserForm'
import {AvatarListItemLink} from '../../../../components/list/ListItem';
import {Avatar} from '../../../../components/Avatar';
import * as Constants from '../../../../constants/ComponentTypes'

class CreateUser extends Component {
  constructor(props) {
    super(props);
    this.state = {openCreate: false}
  }

  componentDidMount() {
    this.props.fetchOrganizations();
  }

  handleOpenCreate() {
    this.setState({openCreate: true})
  }

  handleCloseCreate() {
    this.setState({openCreate: false})
  }

  onSubmitCreate(data) {
    if( typeof data['user_organization'] === 'object' ) {
      data['user_organization'] = data['user_organization']['organization_id']
      this.props.addUser(data)
    } else {
      let orgData = {organization_name: data['user_organization']}
      data['user_organization'] = this.props.addOrganization(orgData)
      //this.props.addUser(data)
    }
  }

  submitFormCreate() {
    this.refs.userForm.submit()
    this.handleCloseCreate()
  }

  render() {
    const actionsCreateUser = [
      <FlatButton
        label="Cancel"
        primary={true}
        onTouchTap={this.handleCloseCreate.bind(this)}
      />,
      <FlatButton
        label="Create user"
        primary={true}
        onTouchTap={this.submitFormCreate.bind(this)}
      />,
    ];

    return (
      <div>
        <AvatarListItemLink
          key="create"
          onClick={this.handleOpenCreate.bind(this)}
          label="Create a new user"
          leftAvatar={<Avatar type={Constants.AVATAR_TYPE_LIST}
                              src="https://www.gravatar.com/avatar/00000000?d=mm&f=y"/>}
        />
        <Dialog
          title="Create a new user"
          modal={false}
          open={this.state.openCreate}
          onRequestClose={this.handleCloseCreate.bind(this)}
          actions={actionsCreateUser}
        >
          <UserForm ref="userForm" onSubmit={this.onSubmitCreate.bind(this)} organizations={this.props.organizations} />
        </Dialog>
      </div>
    );
  }
}

CreateUser.propTypes = {
  exerciseId: PropTypes.string,
  organizations: PropTypes.object,
  fetchOrganizations: PropTypes.func,
  addOrganization: PropTypes.func,
  addUser: PropTypes.func
}

const select = (state) => {
  return {
    organizations: state.application.getIn(['entities', 'organizations']),
  }
}

export default connect(select, {fetchOrganizations, addOrganization, addUser})(CreateUser);
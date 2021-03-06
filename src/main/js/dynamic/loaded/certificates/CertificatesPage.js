import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { clearCertificateFieldErrors, deleteCertificate, fetchCertificates, saveCertificate } from 'store/actions/certificates';
import ConfigurationLabel from 'component/common/ConfigurationLabel';
import TableDisplay from 'field/TableDisplay';
import TextInput from 'field/input/TextInput';
import TextArea from 'field/input/TextArea';
import ReadOnlyField from 'field/ReadOnlyField';
import * as DescriptorUtilities from 'util/descriptorUtilities';

class CertificatesPage extends Component {
    constructor(props) {
        super(props);
        this.retrieveData = this.retrieveData.bind(this);
        this.clearModalFieldState = this.clearModalFieldState.bind(this);
        this.createColumns = this.createColumns.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.onSave = this.onSave.bind(this);
        this.onConfigClose = this.onConfigClose.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.createModalFields = this.createModalFields.bind(this);
        this.onEdit = this.onEdit.bind(this);
        this.onCopy = this.onCopy.bind(this);

        this.state = {
            certificate: {},
            saveCallback: () => null,
            deleteCallback: () => null
        };
    }

    componentDidUpdate(prevProps) {
        if (prevProps.saveStatus === 'SAVING' && (this.props.saveStatus === 'SAVED' || this.props.saveStatus === 'ERROR')) {
            this.state.saveCallback(true);
        }
        if (prevProps.inProgress && !prevProps.deleteSuccess && !this.props.inProgress && this.props.deleteSuccess) {
            this.state.deleteCallback();
        }
    }

    createColumns() {
        return [
            {
                header: 'id',
                headerLabel: 'Id',
                isKey: true,
                hidden: true
            },
            {
                header: 'alias',
                headerLabel: 'Alias',
                isKey: false,
                hidden: false
            },
            {
                header: 'lastUpdated',
                headerLabel: 'Last Updated',
                isKey: false,
                hidden: false
            }

        ];
    }

    onConfigClose(callback) {
        this.props.clearFieldErrors();
        callback();
    }

    clearModalFieldState() {
        if (this.state.certificate && Object.keys(this.state.certificate).length > 0) {
            this.setState({
                certificate: {}
            });
        }
    }

    retrieveData() {
        this.props.getCertificates();
    }

    handleChange(e) {
        const { name, value, type, checked } = e.target;
        const { certificate } = this.state;

        const updatedValue = type === 'checkbox' ? checked.toString().toLowerCase() === 'true' : value;
        const newCertificate = Object.assign(certificate, { [name]: updatedValue });
        this.setState({
            certificate: newCertificate
        });
    }

    onSave(callback) {
        const { certificate } = this.state;
        this.props.saveCertificate(certificate);
        this.setState({
            saveCallback: callback
        });
        return true;
    }

    onDelete(certificatesToDelete, callback) {
        if (certificatesToDelete) {
            certificatesToDelete.forEach(certificateId => {
                this.props.deleteCertificate(certificateId);
            });
        }
        this.setState({
            deleteCallback: callback
        });
    }

    createModalFields() {
        const { certificate } = this.state;
        const { fieldErrors } = this.props;
        const aliasKey = 'alias';
        const certificateContentKey = 'certificateContent';
        return (
            <div>
                <ReadOnlyField label="Last Updated" name="lastUpdated" readOnly="true" value={certificate['lastUpdated']} />
                <TextInput
                    name={aliasKey} label="Alias" description="The certificate alias name."
                    required onChange={this.handleChange} value={certificate[aliasKey]}
                    errorName={aliasKey}
                    errorValue={fieldErrors[aliasKey]} />
                <TextArea
                    name={certificateContentKey} label="Certificate Content" description="The certificate content text."
                    required onChange={this.handleChange} value={certificate[certificateContentKey]}
                    errorName={certificateContentKey}
                    errorValue={fieldErrors[certificateContentKey]} />
            </div>
        );
    }

    onEdit(selectedRow, callback) {
        this.setState({
            certificate: selectedRow
        }, callback);
    }

    onCopy(selectedRow, callback) {
        selectedRow.id = null;
        this.setState({
            certificate: selectedRow
        }, callback);
    }

    render() {
        const { fetching, inProgress, certificates, certificateDeleteError, label, description, fieldErrors, descriptors } = this.props;

        const descriptor = DescriptorUtilities.findFirstDescriptorByNameAndContext(descriptors, DescriptorUtilities.DESCRIPTOR_NAME.COMPONENT_CERTIFICATES, DescriptorUtilities.CONTEXT_TYPE.GLOBAL)
        const hasFieldErrors = fieldErrors && Object.keys(fieldErrors).length > 0;
        const canCreate = DescriptorUtilities.isOperationAssigned(descriptor, DescriptorUtilities.OPERATIONS.CREATE);
        const canDelete = DescriptorUtilities.isOperationAssigned(descriptor, DescriptorUtilities.OPERATIONS.DELETE);
        const canSave = DescriptorUtilities.isOperationAssigned(descriptor, DescriptorUtilities.OPERATIONS.WRITE);
        return (
            <div>
                <div>
                    <ConfigurationLabel configurationName={label} description={description} />
                </div>
                <div>
                    <TableDisplay
                        newConfigFields={this.createModalFields}
                        modalTitle="Certificate"
                        clearModalFieldState={this.clearModalFieldState}
                        onConfigSave={this.onSave}
                        onConfigDelete={this.onDelete}
                        onConfigClose={this.onConfigClose}
                        onConfigCopy={this.onCopy}
                        onEditState={this.onEdit}
                        refreshData={this.retrieveData}
                        data={certificates}
                        columns={this.createColumns()}
                        newButton={canCreate}
                        deleteButton={canDelete}
                        saveButton={canSave}
                        hasFieldErrors={hasFieldErrors}
                        errorDialogMessage={certificateDeleteError}
                        inProgress={inProgress}
                        fetching={fetching}
                        enableCopy={false}
                    />
                </div>
            </div>
        );
    }
}

CertificatesPage.propTypes = {
    descriptors: PropTypes.arrayOf(PropTypes.object).isRequired,
    certificates: PropTypes.arrayOf(PropTypes.object),
    saveCertificate: PropTypes.func.isRequired,
    deleteCertificate: PropTypes.func.isRequired,
    getCertificates: PropTypes.func.isRequired,
    clearFieldErrors: PropTypes.func.isRequired,
    certificateDeleteError: PropTypes.string,
    inProgress: PropTypes.bool,
    deleteSuccess: PropTypes.bool,
    fetching: PropTypes.bool,
    fieldErrors: PropTypes.object,
    description: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    saveStatus: PropTypes.string.isRequired
};

CertificatesPage.defaultProps = {
    inProgress: false,
    deleteSuccess: false,
    certificateDeleteError: '',
    fetching: false,
    certificates: [],
    fieldErrors: {}
};

const mapStateToProps = state => ({
    descriptors: state.descriptors.items,
    certificates: state.certificates.data,
    certificateDeleteError: state.certificates.certificateDeleteError,
    inProgress: state.certificates.inProgress,
    fetching: state.certificates.fetching,
    fieldErrors: state.certificates.fieldErrors,
    saveStatus: state.certificates.saveStatus,
    deleteSuccess: state.certificates.deleteSuccess
});

const mapDispatchToProps = dispatch => ({
    saveCertificate: certificate => dispatch(saveCertificate(certificate)),
    deleteCertificate: certificateId => dispatch(deleteCertificate(certificateId)),
    getCertificates: () => dispatch(fetchCertificates()),
    clearFieldErrors: () => dispatch(clearCertificateFieldErrors())
});

export default connect(mapStateToProps, mapDispatchToProps)(CertificatesPage);

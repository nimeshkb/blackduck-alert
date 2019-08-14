import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { connect } from "react-redux";
import { createNewConfigurationRequest } from "../util/configurationRequestBuilder";
import DynamicSelectInput from "./input/DynamicSelect";

class ProviderDataSelectField extends Component {
    constructor(props) {
        super(props);

        this.onSendClick = this.onSendClick.bind(this);
        this.state = ({
            options: [],
            progress: false,
        });
    }

    onSendClick() {
        this.setState({
            fieldError: this.props.errorValue,
            progress: true,
            success: false
        });
        const {
            fieldKey, csrfToken, currentConfig, endpoint
        } = this.props;

        const request = createNewConfigurationRequest(`/alert${endpoint}/${fieldKey}`, csrfToken, currentConfig);
        request.then((response) => {
            this.setState({
                progress: false
            });
            if (response.ok) {
                response.json().then((data) => {
                    const options = data.map(item => {
                        const dataValue = item.value;
                        return { icon: null, key: dataValue, label: dataValue, value: dataValue };
                    });

                    this.setState({
                        options,
                        success: true
                    })
                });

            } else {
                response.json().then((data) => {
                    this.setState({
                        options: [],
                        fieldError: data.message
                    });
                });
            }
        });
    }

    render() {
        return (
            <div>
                <DynamicSelectInput onChange={this.props.onChange} onFocus={this.onSendClick} options={this.state.options} {...this.props} />
                <div className="progressContainer">
                    <div className="progressIcon">
                        {this.state.progress &&
                        <FontAwesomeIcon icon="spinner" className="alert-icon" size="lg" spin />
                        }
                    </div>
                </div>
            </div>
        );
    }
}

ProviderDataSelectField.propTypes = {
    id: PropTypes.string,
    providerDataEndpoint: PropTypes.string,
    currentConfig: PropTypes.object,
    inputClass: PropTypes.string,
    labelClass: PropTypes.string,
    selectSpacingClass: PropTypes.string,
    components: PropTypes.object,
    value: PropTypes.array,
    placeholder: PropTypes.string,
    searchable: PropTypes.bool,
    removeSelected: PropTypes.bool,
    multiSelect: PropTypes.bool,
    onChange: PropTypes.func.isRequired,
    endpoint: PropTypes.string.isRequired,
    fieldKey: PropTypes.string.isRequired
};

ProviderDataSelectField.defaultProps = {
    id: 'id',
    value: [],
    currentConfig: {},
    placeholder: 'Choose a value',
    components: {},
    inputClass: 'typeAheadField',
    labelClass: 'col-sm-3',
    selectSpacingClass: 'col-sm-8',
    searchable: false,
    removeSelected: false,
    multiSelect: false
};

const mapStateToProps = state => ({
    csrfToken: state.session.csrfToken
});

export default connect(mapStateToProps, null)(ProviderDataSelectField);

const path = require('path')
const fs = require('fs')
const HtmlWebpackPlugin = require('html-webpack-plugin')

const appDirectory = fs.realpathSync(process.cwd())
const resolveAppPath = (relativePath) => path.resolve(appDirectory, relativePath)

const host = process.env.HOST || 'localhost'

process.env.NODE_ENV = 'development'

module.exports = {
    mode: 'development',
    entry: resolveAppPath('src'),
    resolve: {
        extensions: ['.js', '.jsx'],
    },
    output: {
        path: resolveAppPath('dist'),
        filename: 'app.js',
    },

    devServer: {
        compress: false,
        hot: true,
        host,
        port: 3000,
        proxy: {
            '/api/*': {
                target: 'http://localhost:8080',
                pathRewrite: { '^/api': '' },
            },
        },
    },

    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                include: resolveAppPath('src'),
                loader: 'babel-loader',
                options: {
                    presets: [
                        [
                            '@babel/preset-env',
                            {
                                targets: {
                                    browsers: ['> 5% in KR', 'last 2 chrome versions'],
                                },
                                debug: true,
                            },
                        ],
                        ['@babel/preset-react', { runtime: 'automatic' }],
                    ],
                    plugins: ['@babel/plugin-proposal-class-properties', 'react-hot-loader/babel'],
                },
            },
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
        ],
    },

    plugins: [
        new HtmlWebpackPlugin({
            inject: true,
            template: resolveAppPath('public/index.html'),
        }),
    ],
}
